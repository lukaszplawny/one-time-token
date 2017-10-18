package com.lukasz.plawny.onetimetoken.dao;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.cassandraunit.spring.EmbeddedCassandra;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.lukasz.plawny.onetimetoken.dbconfig.CassandraConfig;
import com.lukasz.plawny.onetimetoken.dto.Token;

@RunWith(SpringRunner.class)
@SpringBootTest({ "spring.data.cassandra.port=9142", "spring.data.cassandra.keyspace-name=testKeyspace" })
@EnableAutoConfiguration
@ComponentScan
@ContextConfiguration(classes = CassandraConfig.class)
@EmbeddedCassandra(timeout = 200000L)
public class TokenDaoImplIntegrationTest {

	@Autowired
	private TokenDaoImpl tokenDao;
	
	@Autowired
	private CassandraOperations cassandraOperations;
	
	@After
	public void clearTokensData() {
		cassandraOperations.truncate("tokens");
	}

	@Test
	public void create_ShouldSaveTokenInDb() throws MalformedURLException{
		Token token = new Token();
		token.setTokenId("validId");
		token.setUrl(new URL("http://www.google.com"));
		tokenDao.create(token);
		Token tokenFromDb = cassandraOperations.selectOneById(Token.class, "validId");
		assertEquals(token, tokenFromDb);
	}

	@Test
	public void find_ShouldReturnSavedToken() throws MalformedURLException{
		Token token = new Token();
		token.setTokenId("validId");
		token.setUrl(new URL("http://www.google.com"));
		tokenDao.create(token);
		Token tokenFromDb = tokenDao.find("validId");
		assertEquals(token, tokenFromDb);
	}

	@Test
	public void find_ShouldReturnNull_IfNoTokenWithSpecifiedId() {
		Token token = tokenDao.find("invalidId");
		assertNull(token);
	}
	
	@Test
	public void tokenShouldBeInvalidAfterTtl() throws MalformedURLException, InterruptedException{
		Token token = new Token();
		token.setTokenId("validId");
		token.setUrl(new URL("http://www.google.com"));
		tokenDao.create(token);
		Thread.sleep(20000);
		Token tokenFromDb = tokenDao.find("validId");
		assertNull(tokenFromDb);
	}
}
