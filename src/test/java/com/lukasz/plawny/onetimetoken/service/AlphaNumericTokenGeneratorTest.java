package com.lukasz.plawny.onetimetoken.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AlphaNumericTokenGeneratorTest {
	
	private static final int CUSTOM_TOKEN_LENGTH = 24;

	private AlphaNumericTokenGenerator tokenGenerator;

	@Before
	public void initTokenGenerator() {
		tokenGenerator = new AlphaNumericTokenGenerator();
	}
	
	@Test
	public void generateToken_ShouldReturnTokenWithSpecifiedNumberOfCharacters() {
		String token = tokenGenerator.generateToken(CUSTOM_TOKEN_LENGTH);
		assertEquals(CUSTOM_TOKEN_LENGTH, token.length());
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateToken_ShouldThrowIllegalArgumentException_IfTokenLengthIsNegative() {
		tokenGenerator.generateToken(-1);
	}
	
	@Test
	public void generateToken_ShouldGenerateTokenFromAlphanumericCharacters() {
		String token = tokenGenerator.generateToken(CUSTOM_TOKEN_LENGTH);
		assertTrue(token.matches("[A-Za-z0-9]+"));
	}

}
