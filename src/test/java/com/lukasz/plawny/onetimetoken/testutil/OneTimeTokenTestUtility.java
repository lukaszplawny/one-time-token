package com.lukasz.plawny.onetimetoken.testutil;

import java.net.MalformedURLException;
import java.net.URL;

import com.lukasz.plawny.onetimetoken.dto.Token;


public final class OneTimeTokenTestUtility {
	
	private OneTimeTokenTestUtility() {
	}
	
	public static final String GOOGLE_URL = "http://www.google.com";
	public static final String INVALID_URL = "invalidurl";
	public static final String VALID_TOKEN_ID = "validTokenId";
	public static final String INVALID_TOKEN_ID = "invalidTokenId";
	public static final String TOKEN_REST_ENDPOINT = "/token";
	
	public static Token createPredefinedTokenForGoogleUrl() throws MalformedURLException {
		Token predefinedToken = new Token();
		predefinedToken.setTokenId(VALID_TOKEN_ID);
		predefinedToken.setUrl(new URL(GOOGLE_URL));
		return predefinedToken;
	}

}