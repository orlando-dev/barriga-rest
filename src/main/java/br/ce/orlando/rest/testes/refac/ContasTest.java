package br.ce.orlando.rest.testes.refac;

import static io.restassured.RestAssured.given;

import org.hamcrest.Matchers;
import org.junit.Test;

import br.ce.orlando.rest.core.BaseTest;
import br.ce.orlando.rest.utils.BarrigaUtils;

public class ContasTest extends BaseTest{
	
	@Test
	public void deveIncluirContaComSucesso() {
		given()
			.body("{ \"nome\": \"Conta inserida\" }")
			.log().all()
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para alterar");
		
		given()
			.body("{ \"nome\": \"Conta alterada\" }")
			.log().all()
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", Matchers.is("Conta alterada"))
			.body("visivel", Matchers.is(true))
			.body("id", Matchers.is(CONTA_ID))
			.body("usuario_id", Matchers.is(30060))
		;
	}
	
	@Test
	public void naoDeveInserirContaMesmoNome() {
		given()
			.body("{ \"nome\": \"Conta mesmo nome\" }")
			.log().all()
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", Matchers.is("Já existe uma conta com esse nome!"))
		;
	}
}
