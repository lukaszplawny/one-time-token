package com.lukasz.plawny.onetimetoken.service;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.lukasz.plawny.onetimetoken.dao.TokenDao;
import com.lukasz.plawny.onetimetoken.dto.Token;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleTokenServiceTest {
	
	@MockBean
	private TokenDao tokenDao;
	
	@MockBean
	private TokenGenerator tokenGenerator;
	
	@Autowired
	private TokenService tokenService;
	
	private Token predefinedToken;
	
	@Before
	public void init() throws MalformedURLException{
		predefinedToken = new Token();
		predefinedToken.setTokenId("tokenId");
		predefinedToken.setUrl(new URL("http://www.google.com"));
		Mockito.when(tokenDao.create(predefinedToken)).thenReturn(predefinedToken);
		Mockito.when(tokenDao.find("tokenid")).thenReturn(predefinedToken);
		Mockito.when(tokenDao.find("invalidtokenid")).thenReturn(null);
	}

	@Test
	public void shouldGenerateTokenForUrl() throws MalformedURLException{
		URL url = new URL("http://www.google.com");
		Token token = tokenService.generateToken(url);
		assertEquals(predefinedToken, token);
	}
	
	@Test
	public void shouldReturnNull_WhenTokenNotFound() {
		Token token = tokenService.findToken("invalidtokenid");
		assertEquals(null, token);
	}
	
	@Test
	public void shouldReturnToken_WhenTokenFound() {
		Token token = tokenService.findToken("tokenid");
		assertEquals(predefinedToken, token);
		
	}

}