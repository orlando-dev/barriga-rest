package br.ce.orlando.rest.utils;

import io.restassured.RestAssured;

public class BarrigaUtils {
	
	public static Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
	
	public static Integer getMoveIdPelaDescricao(String desc) {
		return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
	}
	
}
