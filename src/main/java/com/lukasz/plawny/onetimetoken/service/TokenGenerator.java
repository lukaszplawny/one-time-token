package com.lukasz.plawny.onetimetoken.service;

public interface TokenGenerator {
	
	String generateToken(int numberOfCharacters);

}