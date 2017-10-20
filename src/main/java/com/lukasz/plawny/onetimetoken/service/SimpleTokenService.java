package com.lukasz.plawny.onetimetoken.service;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lukasz.plawny.onetimetoken.dao.TokenDao;
import com.lukasz.plawny.onetimetoken.dto.Token;

@Service
public class SimpleTokenService implements TokenService {

	private static final int TOKEN_LENGTH = 12;
	private static final Logger logger = LoggerFactory.getLogger(SimpleTokenService.class);

	private final TokenGenerator tokenGenerator;
	private final TokenDao tokenDao;

	@Autowired
	public SimpleTokenService(TokenGenerator tokenGenerator, TokenDao tokenDao) {
		this.tokenGenerator = tokenGenerator;
		this.tokenDao = tokenDao;
	}

	@Override
	public Token createToken(URL url) {
		if (url == null)
			throw new IllegalArgumentException("The url cannot be null");
		Token token = new Token();
		token.setUrl(url);
		String tokenId = tokenGenerator.generateToken(TOKEN_LENGTH);
		token.setTokenId(tokenId);
		logger.info("New token generated: " + tokenId + " for url " + url);
		return tokenDao.create(token);
	}

	@Override
	public Token findToken(String tokenId) {
		return tokenDao.find(tokenId);
	}
}