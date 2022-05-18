package br.ce.orlando.rest.testes;

import static io.restassured.RestAssured.given;

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
	
}
