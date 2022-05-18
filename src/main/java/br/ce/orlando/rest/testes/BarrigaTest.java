package br.ce.orlando.rest.testes;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import br.ce.orlando.rest.core.BaseTest;

public class BarrigaTest extends BaseTest{
	
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
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "orlando.dev@hotmail.com");
		login.put("senha", "1234");
		
		String token = given()
		.when()
			.log().all()
			.body(login)
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
		
		given()
			.header("Authorization", "JWT " + token)
			.body("{ \"nome\": \"uma conta\" }")
			.log().all()
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
	
}
