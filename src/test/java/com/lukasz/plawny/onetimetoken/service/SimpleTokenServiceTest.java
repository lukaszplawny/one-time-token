package com.lukasz.plawny.onetimetoken.service;

import static com.lukasz.plawny.onetimetoken.testutil.OneTimeTokenTestUtility.*;
import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.lukasz.plawny.onetimetoken.dao.TokenDao;
import com.lukasz.plawny.onetimetoken.dto.Token;

@RunWith(MockitoJUnitRunner.class)
public class SimpleTokenServiceTest {

	private static final int DEFAULT_TOKEN_LENGTH = 12;

	@Mock
	private TokenDao tokenDao;

	@Mock
	private TokenGenerator tokenGenerator;

	private TokenService tokenService;
	private Token predefinedToken;

	@Before
	public void setUp() throws MalformedURLException {
		tokenService = new SimpleTokenService(tokenGenerator, tokenDao);
		predefinedToken = createPredefinedTokenForGoogleUrl();
		Mockito.when(tokenDao.create(predefinedToken)).thenReturn(predefinedToken);
		Mockito.when(tokenDao.find(VALID_TOKEN_ID)).thenReturn(predefinedToken);
		Mockito.when(tokenDao.find(INVALID_TOKEN_ID)).thenReturn(null);
		Mockito.when(tokenGenerator.generateToken(DEFAULT_TOKEN_LENGTH)).thenReturn(VALID_TOKEN_ID);
	}

	@Test
	public void createToken_ShouldCreateTokenForUrl() throws MalformedURLException {
		URL url = new URL(GOOGLE_URL);
		Token token = tokenService.createToken(url);
		assertEquals(VALID_TOKEN_ID, token.getTokenId());
		assertEquals(url, token.getUrl());
	}

	@Test
	public void findToken_ShouldReturnNull_WhenTokenNotFound() {
		Token token = tokenService.findToken(INVALID_TOKEN_ID);
		assertNull(token);
	}

	@Test
	public void findToken_ShouldReturnToken_WhenTokenFound() {
		Token token = tokenService.findToken(VALID_TOKEN_ID);
		assertEquals(predefinedToken, token);
	}
}