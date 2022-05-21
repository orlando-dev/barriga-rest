package br.ce.orlando.rest.testes.refac;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.orlando.rest.core.BaseTest;
import io.restassured.RestAssured;

public class SaldoTest extends BaseTest{
	
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
	public void deveCalcularSaldoContas() {
		Integer CONTA_ID = getIdContaPeloNome("Conta para saldo");
		
		given()
			.log().all()
		.when()
			.get("/saldo")
		.then()
			.log().all()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", Matchers.is("534.00"))
		;
	}
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
	
	public Integer getMoveIdPelaDescricao(String desc) {
		return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
	}
}
