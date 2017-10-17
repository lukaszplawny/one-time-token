package com.lukasz.plawny.onetimetoken.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class RandomTokenGenerator implements TokenGenerator {

	private static final String UPPER_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWER_LETTER = UPPER_LETTER.toLowerCase();
	private static final String DIGITS = "0123456789";
	private static final String AVAILABLE_CHARACTERS = UPPER_LETTER + LOWER_LETTER + DIGITS;

	@Override
	public String generateToken(int numberOfCharacters) {
		SecureRandom secureRandom = new SecureRandom();
		StringBuffer tokenBuffer = new StringBuffer();
		for (int i = 0; i < numberOfCharacters; i++)
			tokenBuffer.append(AVAILABLE_CHARACTERS.charAt(secureRandom.nextInt(AVAILABLE_CHARACTERS.length())));
		return tokenBuffer.toString();
	}

}