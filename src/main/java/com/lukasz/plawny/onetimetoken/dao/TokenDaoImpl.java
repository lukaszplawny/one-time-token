package com.lukasz.plawny.onetimetoken.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.core.WriteOptions;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.lukasz.plawny.onetimetoken.dto.Token;

@Repository
public class TokenDaoImpl implements TokenDao {
	
	private static int DEFAULT_TOKEN_TTL = 20;

	@Autowired
	private CassandraOperations cassandraOperations;

	private final WriteOptions writeOptions;

	@Autowired
	public TokenDaoImpl(@Value(("${token.ttl:20}")) int ttl) {
		writeOptions = new WriteOptions();
		if (ttl > 0)
			writeOptions.setTtl(ttl);
		else
			writeOptions.setTtl(DEFAULT_TOKEN_TTL);
	}

	public Token create(Token token) {
		return cassandraOperations.insert(token, writeOptions);
	}

	public Token find(String tokenId) {
		return cassandraOperations.selectOneById(Token.class, tokenId);
	}

}