package com.lukasz.plawny.onetimetoken.service;

import java.net.URL;

import com.lukasz.plawny.onetimetoken.dto.Token;

public interface TokenService {
	
	Token generateToken(URL url);
	Token findToken(String tokenId);

}
