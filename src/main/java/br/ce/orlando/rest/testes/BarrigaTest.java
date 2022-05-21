package br.ce.orlando.rest.testes;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import br.ce.orlando.rest.core.BaseTest;

public class BarrigaTest extends BaseTest{
	
	private String TOKEN;
	
	@Before
	public void login() {
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "orlando.dev@hotmail.com");
		login.put("senha", "1234");
		
		TOKEN = given()
		.when()
			.log().all()
			.body(login)
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
	}
	
	@Test
	public void naoDeveAcessarAPISemToken() {
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
	
	@Test
	public void deveIncluirContaComSucesso() {
		given()
			.header("Authorization", "JWT " + TOKEN )
			.body("{ \"nome\": \"uma conta\" }")
			.log().all()
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{ \"nome\": \"conta alterada\" }")
			.log().all()
		.when()
			.put("/contas/1177425")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", Matchers.is("conta alterada"))
			.body("visivel", Matchers.is(true))
			.body("id", Matchers.is(1177425))
			.body("usuario_id", Matchers.is(30060))
		;
	}
	
	@Test
	public void naoDeveInserirContaMesmoNome() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{ \"nome\": \"conta alterada\" }")
			.log().all()
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", Matchers.is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void deveInserirMovimentacaoSucesso() {
		Movimentacao move = getMovimentacao();
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.log().all()
			.body(move)
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(201)
		;
	}
	
	@Test
	public void deveValidarCamposObrigatoriosMovimentacao() {
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{}")
			.log().all()
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", Matchers.hasSize(8))
			.body("msg", Matchers.hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"))
		;
	}
	
	@Test
	public void naoDeveInserirMovimentacaDataFutura() {
		Movimentacao move = getMovimentacao();
		move.setData_transacao("21/05/2023");
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.log().all()
			.body(move)
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(400)
			.body("$", Matchers.hasSize(1))
			.body("msg", Matchers.hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}
	

	private Movimentacao getMovimentacao() {
		Movimentacao move = new Movimentacao();
		move.setConta_id(1177425);
//		move.setUsuario_id(usuario_id);
		move.setDescricao("Descrição da movimentação");
		move.setEnvolvido("Envolvido na move");
		move.setTipo("REC");
		move.setData_transacao("01/01/2000");
		move.setData_pagamento("10/05/2010");
		move.setValor(100f);
		move.setStatus(true);
		return move;
	}
	
	
	
}
