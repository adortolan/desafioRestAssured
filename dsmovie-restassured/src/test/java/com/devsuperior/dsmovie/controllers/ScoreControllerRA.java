package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class ScoreControllerRA {
	private String adminUserName, adminPassword;
	private String tokenAdmin;
	private Long notFoundId;
	private Map<String, Object> postScore;

	@BeforeEach
	public void setUp() throws JSONException {
		adminUserName = "maria@gmail.com";
		adminPassword = "123456";
		baseURI = "http://localhost:8080";
		notFoundId = 1000L;

		tokenAdmin = TokenUtil.obtainAccessToken(adminUserName, adminPassword);
		postScore = new HashMap<>();
		postScore.put("movieId", notFoundId);
		postScore.put("score", 3.0);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		JSONObject newScore = new JSONObject(postScore);
		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + tokenAdmin)
				.body(newScore)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.assertThat()
				.statusCode(404);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		postScore.remove("movieId");
		JSONObject newScore = new JSONObject(postScore);
		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + tokenAdmin)
				.body(newScore)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.assertThat()
				.statusCode(422);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		postScore.put("score", -1.0);
		JSONObject newScore = new JSONObject(postScore);
		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + tokenAdmin)
				.body(newScore)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.assertThat()
				.statusCode(422);
	}
}
