package br.ce.orlando.rest.testes;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.ce.orlando.rest.core.BaseTest;
import br.ce.orlando.rest.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest{
	
	private static String CONTA_NAME = "Conta " + System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOVE_ID;
	
	@BeforeClass
	public static void login() {
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "orlando.dev@hotmail.com");
		login.put("senha", "1234");
		
		String TOKEN = given()
		.when()
			.log().all()
			.body(login)
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token");
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
	}
	
	
	@Test
	public void t02_deveIncluirContaComSucesso() {
		CONTA_ID = given()
			.body("{ \"nome\": \""+CONTA_NAME+"\" }")
			.log().all()
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t03_deveAlterarContaComSucesso() {
		given()
			.body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
			.log().all()
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", Matchers.is(CONTA_NAME+" alterada"))
			.body("visivel", Matchers.is(true))
			.body("id", Matchers.is(CONTA_ID))
			.body("usuario_id", Matchers.is(30060))
		;
	}
	
	@Test
	public void t04_naoDeveInserirContaMesmoNome() {
		given()
			.body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
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
	public void t05_deveInserirMovimentacaoSucesso() {
		Movimentacao move = getMovimentacao();
		
		MOVE_ID = given()
			.log().all()
			.body(move)
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t06_deveValidarCamposObrigatoriosMovimentacao() {
		given()
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
	public void t07_naoDeveInserirMovimentacaComDataFutura() {
		Movimentacao move = getMovimentacao();
		move.setData_transacao(DataUtils.getDataDiferencaDias(2));
		
		given()
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
	
	
	@Test
	public void t08_naoDeveRemoverContaComMovimentaca() {

		given()
			.log().all()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.log().all()
			.statusCode(500)
			.body("constraint", Matchers.is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void t09_deveCalcularSaldoContas() {

		given()
			.log().all()
		.when()
			.get("/saldo")
		.then()
			.log().all()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", Matchers.is("100.00"))
		;
	}
	
	@Test
	public void t10_deveRemoverMovimentacao() {

		given()
			.log().all()
			.pathParam("id", MOVE_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.log().all()
			.statusCode(204)
		;
	}

	//Esse teste não deve usar token
	@Test
	public void t11_naoDeveAcessarAPISemToken() {
		FilterableRequestSpecification request = (FilterableRequestSpecification) RestAssured.requestSpecification;
		request.removeHeader("Authorization");
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
	private Movimentacao getMovimentacao() {
		Movimentacao move = new Movimentacao();
		move.setConta_id(CONTA_ID);
//		move.setUsuario_id(usuario_id);
		move.setDescricao("Descrição da movimentação");
		move.setEnvolvido("Envolvido na move");
		move.setTipo("REC");
		move.setData_transacao(DataUtils.getDataDiferencaDias(-1));
		move.setData_pagamento(DataUtils.getDataDiferencaDias(5));
		move.setValor(100f);
		move.setStatus(true);
		return move;
	}
	
}
