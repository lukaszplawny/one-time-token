package com.lukasz.plawny.onetimetoken.service;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lukasz.plawny.onetimetoken.dao.TokenDao;
import com.lukasz.plawny.onetimetoken.dto.Token;

@Service
public class SimpleTokenService implements TokenService {

	private static final int TOKEN_LENGTH = 5;

	private final TokenGenerator tokenGenerator;
	private final TokenDao tokenDao;

	@Autowired
	public SimpleTokenService(TokenGenerator tokenGenerator, TokenDao tokenDao) {
		this.tokenGenerator = tokenGenerator;
		this.tokenDao = tokenDao;
	}

	// check if this synchronized is sufficient
	@Override
	public Token generateToken(URL url) {
		String tokenId;
		Token token = new Token();
		token.setUrl(url);
		synchronized (TokenService.class) {
			do {
				tokenId = tokenGenerator.generateToken(TOKEN_LENGTH);
			} while (tokenDao.find(tokenId) != null);

			token.setTokenId(tokenId);
			tokenDao.create(token);
		}
		return token;
	}

	@Override
	public Token findToken(String tokenId) {
		return tokenDao.find(tokenId);
	}

}