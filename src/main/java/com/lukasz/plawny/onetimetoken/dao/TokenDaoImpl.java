package com.lukasz.plawny.onetimetoken.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.core.WriteOptions;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.lukasz.plawny.onetimetoken.dto.Token;

@Repository
public class TokenDaoImpl implements TokenDao{
	
	@Autowired
	private CassandraOperations cassandraOperations;
	
	private final int ttl;
	
	@Autowired
	public TokenDaoImpl(@Value(("#{ @environment['token.ttl'] ?: 20 }")) int ttl) {
		this.ttl = ttl;
	}

	public Token create(Token token) {
		WriteOptions writeOptions = new WriteOptions();
		writeOptions.setTtl(ttl);
		return cassandraOperations.insert(token, writeOptions);
	}
	
	public Token find(String tokenId) {
		return cassandraOperations.selectOneById(Token.class, tokenId);
	}
	
}