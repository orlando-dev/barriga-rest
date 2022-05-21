package br.ce.orlando.rest.testes.refac;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.orlando.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AutenticacaoTest extends BaseTest{
	
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
