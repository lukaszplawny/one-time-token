package com.lukasz.plawny.onetimetoken.systemtest;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.lukasz.plawny.onetimetoken.dbconfig.CassandraConfig;
import com.lukasz.plawny.onetimetoken.dto.Token;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:config/application.properties")
@SpringBootTest(classes = CassandraConfig.class)
public class ApplicationSystemT {
	
	@Autowired
	private CassandraOperations cassandraOperations;

	@Value("${token.ttl}")
	private int ttl;

	@BeforeClass
	public static void setup() {
		RestAssured.port = Integer.valueOf(8080);
		RestAssured.baseURI = "http://localhost";

	}

	@Test
	public void shouldCreateTokenForUrl_AndRedirectToUrlWhenTokenIsUsed() {
		Response response = RestAssured.given().param("url", "http://www.google.com").when().post("/token").andReturn();
		assertEquals(201, response.statusCode());
		String tokenId = response.getBody().asString();
		
		Token tokenFromDb = cassandraOperations.selectOneById(Token.class, tokenId);
		assertEquals("http://www.google.com", tokenFromDb.getUrl().toString());
		
		RestAssured.given().when().redirects().follow(false).get("/token/" + tokenId)
		.then().statusCode(302).and().header("location", "http://www.google.com");

	}
	
	@Test
	public void shouldRemoveTokenFromDBAfterTtl_AndReturnNotFoundIfTokenUsed() throws InterruptedException {
		Response response = RestAssured.given().param("url", "http://www.google.com").when().post("/token").andReturn();
		assertEquals(201, response.statusCode());
		String tokenId = response.getBody().asString();
		
		Token tokenFromDb = cassandraOperations.selectOneById(Token.class, tokenId);
		assertEquals("http://www.google.com", tokenFromDb.getUrl().toString());
		
		Thread.sleep(ttl * 1000);
		RestAssured.given().when().get("/token/" + tokenId)
		.then().statusCode(404);
		
		tokenFromDb = cassandraOperations.selectOneById(Token.class, tokenId);
		assertNull(tokenFromDb);
	}
	
	public void shouldRedirectToUrlFromToken_UntilTtlReached() throws InterruptedException {
		Response response = RestAssured.given().param("url", "http://www.google.com").when().post("/token").andReturn();
		assertEquals(201, response.statusCode());
		String tokenId = response.getBody().asString();
		
		Token tokenFromDb = cassandraOperations.selectOneById(Token.class, tokenId);
		assertEquals("http://www.google.com", tokenFromDb.getUrl().toString());
		
		for (int i = 1; i < ttl; i++) {
			RestAssured.given().when().redirects().follow(false).get("/token/" + tokenId)
			.then().statusCode(302).and().header("location", "http://www.google.com");
			Thread.sleep(1000);
		}
	}

}
