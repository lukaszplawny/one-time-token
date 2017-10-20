package com.lukasz.plawny.onetimetoken.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.core.WriteOptions;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.lukasz.plawny.onetimetoken.dto.Token;

@Repository
public class TokenDaoImpl implements TokenDao {
	
	private static final int DEFAULT_TOKEN_TTL = 20;
	private static final Logger logger = LoggerFactory.getLogger(TokenDaoImpl.class);

	@Autowired
	private CassandraOperations cassandraOperations;

	private final WriteOptions writeOptions;

	@Autowired
	public TokenDaoImpl(@Value("${token.ttl:20}") int ttl) {
		writeOptions = new WriteOptions();
		if (ttl > 0) {
			writeOptions.setTtl(ttl);
		}
		else {
			writeOptions.setTtl(DEFAULT_TOKEN_TTL);
			logger.warn("TTL is equal or less than zero, using default TTL value.");;
		}
		logger.info("TTL set to " + writeOptions.getTtl());
	}

	public Token create(Token token) {
		return cassandraOperations.insert(token, writeOptions);
	}

	public Token find(String tokenId) {
		return cassandraOperations.selectOneById(Token.class, tokenId);
	}

}