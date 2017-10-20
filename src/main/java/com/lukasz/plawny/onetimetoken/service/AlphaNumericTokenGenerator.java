package com.lukasz.plawny.onetimetoken.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class AlphaNumericTokenGenerator implements TokenGenerator {

	private static final String UPPER_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWER_LETTER = UPPER_LETTER.toLowerCase();
	private static final String DIGITS = "0123456789";
	private static final String AVAILABLE_CHARACTERS = UPPER_LETTER + LOWER_LETTER + DIGITS;

	@Override
	public String generateToken(int numberOfCharacters) {
		if (numberOfCharacters <= 0)
			throw new IllegalArgumentException("Token length must be greater than 0");
		SecureRandom secureRandom = new SecureRandom();
		StringBuilder tokenBuilder = new StringBuilder();
		for (int i = 0; i < numberOfCharacters; i++)
			tokenBuilder.append(randomAlphaNumericCharacter(secureRandom));
		return tokenBuilder.toString();
	}

	private char randomAlphaNumericCharacter(SecureRandom secureRandom) {
		return AVAILABLE_CHARACTERS.charAt(secureRandom.nextInt(AVAILABLE_CHARACTERS.length()));
	}
}