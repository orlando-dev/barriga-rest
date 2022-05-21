package br.ce.orlando.rest.testes.refac;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.ce.orlando.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AutenticacaoTest extends BaseTest{
	
	@Test
	public void naoDeveAcessarAPISemToken() {
		FilterableRequestSpecification request = (FilterableRequestSpecification) RestAssured.requestSpecification;
		request.removeHeader("Authorization");
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
}
