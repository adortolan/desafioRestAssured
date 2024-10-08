package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

public class MovieControllerRA {
	private String clientUsername, clientPassword, adminUserName, adminPassword;
	private String tokenClient;
	private Long movieId, notFoundId;

	@BeforeEach
	public void setUp() throws JSONException {
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUserName = "maria@gmail.com";
		adminPassword = "123456";
		baseURI = "http://localhost:8080";
		movieId = 4L;
		notFoundId = 1000L;

		tokenClient = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given().get("/movies").then().statusCode(200);
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		given().get("/movies?title=Matrix")
				.then()
				.statusCode(200)
				.body("content.id[0]", is(4))
				.body("content.title[0]", equalTo("Matrix Resurrections"));

	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		given().get("/movies/{id}", movieId)
				.then()
				.statusCode(200)
				.body("id", is(4))
				.body("title", equalTo("Matrix Resurrections"))
				.body("score", is(0.0F))
				.body("count", is(0))
				.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/hv7o3VgfsairBoQFAawgaQ4cR1m.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		given().get("/movies/{id}", notFoundId)
				.then()
				.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		given()
			.auth().basic(adminUserName, adminPassword)
			.contentType("application/json")
			.body("{\"title\":\"\",\"score\":0.0,\"count\":0,\"image\":\"https://www.themoviedb.org/t/p/w533_and_h300_bestv2/hv7o3VgfsairBoQFAawgaQ4cR1m.jpg\"}")
		.when()
			.post("/movies")
		.then()
			.statusCode(422);
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		given()
			.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + tokenClient)
			.body("{\"title\":\"The Matrix\",\"score\":0.0,\"count\":0,\"image\":\"https://www.themoviedb.org/t/p/w533_and_h300_bestv2/hv7o3VgfsairBoQFAawgaQ4cR1m.jpg\"}")
		.when()
			.post("/movies")
		.then()
			.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		given()
			.header("Content-type", "application/json")
				.header("Authorization", "Bearer invalid")
			.body("{\"title\":\"The Matrix\",\"score\":0.0,\"count\":0,\"image\":\"https://www.themoviedb.org/t/p/w533_and_h300_bestv2/hv7o3VgfsairBoQFAawgaQ4cR1m.jpg\"}")
		.when()
			.post("/movies")
		.then()
			.statusCode(401);
	}
}
