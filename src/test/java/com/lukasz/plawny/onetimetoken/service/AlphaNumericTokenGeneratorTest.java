package com.lukasz.plawny.onetimetoken.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlphaNumericTokenGeneratorTest {
	
	private static final int CUSTOM_TOKEN_LENGTH = 24;

	@Autowired
	private AlphanumericTokenGenerator tokenGenerator;
	
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
