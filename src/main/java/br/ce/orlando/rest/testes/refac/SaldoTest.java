package br.ce.orlando.rest.testes.refac;

import static io.restassured.RestAssured.given;

import org.hamcrest.Matchers;
import org.junit.Test;

import br.ce.orlando.rest.core.BaseTest;
import br.ce.orlando.rest.utils.BarrigaUtils;

public class SaldoTest extends BaseTest{
	
	@Test
	public void deveCalcularSaldoContas() {
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para saldo");
		
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
}
