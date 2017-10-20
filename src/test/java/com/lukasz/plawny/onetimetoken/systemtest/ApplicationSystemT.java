package com.lukasz.plawny.onetimetoken.systemtest;

import static org.junit.Assert.*;
import static com.lukasz.plawny.onetimetoken.testutil.OneTimeTokenTestUtility.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.lukasz.plawny.onetimetoken.dbconfig.CassandraConfig;
import com.lukasz.plawny.onetimetoken.dto.Token;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:config/application.properties")
@SpringBootTest(classes = CassandraConfig.class)
public class ApplicationSystemT {

	@Autowired
	private CassandraOperations cassandraOperations;

	@Value("${token.ttl:20}")
	private int ttl;

	@BeforeClass
	public static void setup() {
		RestAssured.port = Integer.valueOf(8080);
		RestAssured.baseURI = "http://localhost";

	}

	@Test
	public void shouldCreateTokenForUrl_AndRedirectToUrlWhenTokenIsUsed() {
		Response response = performPostReqestForTokenWithGoogleUrl();
		assertEquals(201, response.statusCode());

		String tokenId = response.getBody().asString();
		Token tokenFromDb = getTokenFromDb(tokenId);
		assertEquals(GOOGLE_URL, tokenFromDb.getUrl().toString());

		RestAssured.given().when().redirects().follow(false).get(TOKEN_REST_ENDPOINT + "/" + tokenId).then().statusCode(302)
				.and().header("location", GOOGLE_URL);

	}

	@Test
	public void shouldRemoveTokenFromDBAfterTtl_AndReturnNotFoundIfTokenUsed() throws InterruptedException {
		Response response = performPostReqestForTokenWithGoogleUrl();
		assertEquals(201, response.statusCode());

		String tokenId = response.getBody().asString();
		Token tokenFromDb = getTokenFromDb(tokenId);
		assertEquals(GOOGLE_URL, tokenFromDb.getUrl().toString());

		Thread.sleep(ttl * 1000);
		RestAssured.given().when().get(TOKEN_REST_ENDPOINT + "/" + tokenId).then().statusCode(404);

		tokenFromDb = getTokenFromDb(tokenId);
		assertNull(tokenFromDb);
	}

	public void shouldRedirectToUrlFromToken_UntilTtlReached() throws InterruptedException {
		Response response = performPostReqestForTokenWithGoogleUrl();
		assertEquals(201, response.statusCode());
		String tokenId = response.getBody().asString();

		Token tokenFromDb = getTokenFromDb(tokenId);
		assertEquals(GOOGLE_URL, tokenFromDb.getUrl().toString());

		for (int i = 1; i < ttl; i++) {
			RestAssured.given().when().redirects().follow(false).get(TOKEN_REST_ENDPOINT + "/" + tokenId).then().statusCode(302).and()
					.header("location", GOOGLE_URL);
			Thread.sleep(1000);
		}
	}

	private Response performPostReqestForTokenWithGoogleUrl() {
		return RestAssured.given().param("url", GOOGLE_URL).when().post(TOKEN_REST_ENDPOINT).andReturn();
	}

	private Token getTokenFromDb(String tokenId) {
		return cassandraOperations.selectOneById(Token.class, tokenId);
	}

}
