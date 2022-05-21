package br.ce.orlando.rest.testes.refac;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.orlando.rest.core.BaseTest;
import io.restassured.RestAssured;

public class ContasTest extends BaseTest{
	
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
		Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");
		
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
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
	
}
