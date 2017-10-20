package com.lukasz.plawny.onetimetoken.service;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AlphaNumericTokenGenerator implements TokenGenerator {

	private static final String UPPER_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWER_LETTER = UPPER_LETTER.toLowerCase();
	private static final String DIGITS = "0123456789";
	private static final String AVAILABLE_CHARACTERS = UPPER_LETTER + LOWER_LETTER + DIGITS;
	private static final Logger LOGGER = LoggerFactory.getLogger(AlphaNumericTokenGenerator.class);
	private final SecureRandom secureRandom;

	public AlphaNumericTokenGenerator() {
		this.secureRandom = new SecureRandom();
	}

	@Override
	public String generateToken(int numberOfCharacters) {
		if (numberOfCharacters <= 0) {
			LOGGER.error("Number of token characters cannot be equals or less than 0. Requested token length: "
					+ numberOfCharacters);
			throw new IllegalArgumentException("Token length must be greater than 0");
		}
		StringBuilder tokenBuilder = new StringBuilder();
		for (int i = 0; i < numberOfCharacters; i++)
			tokenBuilder.append(randomAlphaNumericCharacter(secureRandom));
		return tokenBuilder.toString();
	}

	private char randomAlphaNumericCharacter(SecureRandom secureRandom) {
		return AVAILABLE_CHARACTERS.charAt(secureRandom.nextInt(AVAILABLE_CHARACTERS.length()));
	}
}