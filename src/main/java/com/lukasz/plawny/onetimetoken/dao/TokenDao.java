package com.lukasz.plawny.onetimetoken.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.core.WriteOptions;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.lukasz.plawny.onetimetoken.dto.Token;

//TODO check which class should be extended
@Repository
public class TokenDao {
	
	@Autowired
	private CassandraOperations cassandraOperations;
	
	private final int ttl;
	
	@Autowired
	public TokenDao(@Value(("#{ @environment['onetimetoken.ttl'] ?: 10 }")) int ttl) {
		this.ttl = ttl;
	}

	public Token create(Token token) {
		WriteOptions writeOptions = new WriteOptions();
		writeOptions.setTtl(ttl);
//		InsertOptions insertOptions = InsertOptions.builder().ifNotExists(true).ttl(ttl).build();	
		return cassandraOperations.insert(token, writeOptions);
	}
	
	public Token find(String tokenId) {
		return cassandraOperations.selectOneById(Token.class, tokenId);
	}
	
}