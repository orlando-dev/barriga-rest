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
	
}
