package br.ce.orlando.rest.testes.refac;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.orlando.rest.core.BaseTest;
import br.ce.orlando.rest.testes.Movimentacao;
import br.ce.orlando.rest.utils.DataUtils;
import io.restassured.RestAssured;

public class MovimentacaoTest extends BaseTest{
	
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
		
		RestAssured.get("/reset").then().statusCode(200);
	}
	
	@Test
	public void deveInserirMovimentacaoSucesso() {
		Movimentacao move = getMovimentacao();
		
		given()
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
	public void naoDeveInserirMovimentacaComDataFutura() {
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
	public void naoDeveRemoverContaComMovimentaca() {
		Integer CONTA_ID = getIdContaPeloNome("Conta com movimentacao");
		
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
	public void deveRemoverMovimentacao() {
		Integer MOVE_ID = getMoveIdPelaDescricao("Movimentacao para exclusao");
		
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
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
	
	public Integer getMoveIdPelaDescricao(String desc) {
		return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
	}
	
	private Movimentacao getMovimentacao() {
		Movimentacao move = new Movimentacao();
		move.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
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
