package com.lukasz.plawny.onetimetoken.dto;

import java.io.Serializable;
import java.net.URL;

import org.springframework.data.cassandra.mapping.CassandraType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import com.datastax.driver.core.DataType.Name;

@Table (value = "Tokens")
public class Token implements Serializable {

	@PrimaryKey
	private String tokenId;

	@CassandraType(type = Name.VARCHAR)
	@Column
	private URL url;

	public Token() {
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getTokenId() {
		return this.tokenId;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

}