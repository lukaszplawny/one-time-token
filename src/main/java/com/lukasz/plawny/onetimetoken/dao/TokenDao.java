package com.lukasz.plawny.onetimetoken.dao;

import com.lukasz.plawny.onetimetoken.dto.Token;

public interface TokenDao {

	public Token create(Token token);

	public Token find(String tokenId);

}
