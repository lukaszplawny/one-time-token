package com.lukasz.plawny.onetimetoken.systemtest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import static com.lukasz.plawny.onetimetoken.testutil.OneTimeTokenTestUtility.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@SpringBootTest(classes = CassandraConfig.class)
@TestPropertySource("classpath:config/application.properties")
public class ApplicationSystemT {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSystemT.class);

	@Autowired
	private CassandraOperations cassandraOperations;

	@Value("${token.ttl:20}")
	private int ttl;

	private List<String> createdTokenId;

	@BeforeClass
	public static void setupBeforeClass() {
		RestAssured.port = 8080;
		RestAssured.baseURI = "http://localhost";
	}

	@Before
	public void setup() {
		ttl = ttl <= 0 ? 20 : ttl; // set ttl to default value if value in
									// properties file is less or equal 0
		createdTokenId = new ArrayList<>();
	}

	@After
	public void tearDown() {
		for (String tokenId : createdTokenId) {
			cassandraOperations.deleteById(Token.class, tokenId);
		}
	}

	@Test
	public void shouldCreateTokenForUrl_AndRedirectToUrlWhenTokenIsUsed() {
		Response response = performPostReqestForTokenWithGoogleUrl();
		assertEquals(201, response.statusCode());

		String tokenId = response.getBody().asString();
		createdTokenId.add(tokenId); // add token to a list of created tokens,
										// so it can be removed after testcase

		Token tokenFromDb = getTokenFromDb(tokenId);
		assertEquals(GOOGLE_URL, tokenFromDb.getUrl().toString());

		RestAssured.given().when().redirects().follow(false).get(TOKEN_REST_ENDPOINT + "/" + tokenId).then()
				.statusCode(302).and().header("location", GOOGLE_URL);

	}

	@Test
	public void shouldRemoveTokenFromDBAfterTtl_AndReturnNotFoundIfTokenUsed() throws InterruptedException {
		Response response = performPostReqestForTokenWithGoogleUrl();
		assertEquals(201, response.statusCode());

		String tokenId = response.getBody().asString();
		createdTokenId.add(tokenId); // add token to a list of created tokens,
										// so it can be removed after testcase

		Token tokenFromDb = getTokenFromDb(tokenId);
		assertEquals(GOOGLE_URL, tokenFromDb.getUrl().toString());

		LOGGER.info("Sleeping " + ttl + " seconds");
		Thread.sleep(ttl * 1000);
		RestAssured.given().when().get(TOKEN_REST_ENDPOINT + "/" + tokenId).then().statusCode(404);

		tokenFromDb = getTokenFromDb(tokenId);
		assertNull(tokenFromDb);
	}

	@Test
	public void shouldRedirectToUrlFromToken_UntilTtlReached() throws InterruptedException {
		Response response = performPostReqestForTokenWithGoogleUrl();
		assertEquals(201, response.statusCode());

		String tokenId = response.getBody().asString();
		createdTokenId.add(tokenId); // add token to a list of created tokens,
										// so it can be removed after testcase

		Token tokenFromDb = getTokenFromDb(tokenId);
		assertEquals(GOOGLE_URL, tokenFromDb.getUrl().toString());

		int oneSecondBeforeTokenExpiration = ttl - 1;
		LOGGER.info("Sleeping " + oneSecondBeforeTokenExpiration + " seconds");
		Thread.sleep(1000 * oneSecondBeforeTokenExpiration);
		RestAssured.given().when().redirects().follow(false).get(TOKEN_REST_ENDPOINT + "/" + tokenId).then()
				.statusCode(302).and().header("location", GOOGLE_URL);
	}

	private Response performPostReqestForTokenWithGoogleUrl() {
		return RestAssured.given().param("url", GOOGLE_URL).when().post(TOKEN_REST_ENDPOINT).andReturn();
	}

	private Token getTokenFromDb(String tokenId) {
		return cassandraOperations.selectOneById(Token.class, tokenId);
	}

}