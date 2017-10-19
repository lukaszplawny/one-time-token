package com.lukasz.plawny.onetimetoken.rest;

import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lukasz.plawny.onetimetoken.dto.Token;
import com.lukasz.plawny.onetimetoken.service.TokenService;

@RestController
public class TokenRestControler {

	@Autowired
	private TokenService tokenService;

	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public String createToken(@RequestParam URL url, HttpServletResponse response) {
		Token token = tokenService.createToken(url);
		response.setStatus(HttpServletResponse.SC_CREATED);
		return token.getTokenId();
	}

	@RequestMapping(value = "/token/{tokenId}", method = RequestMethod.GET)
	public void useToken(@PathVariable String tokenId, HttpServletResponse response) {
		Token token = tokenService.findToken(tokenId);
		if (token == null)
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		else {
			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", token.getUrl().toString());
		}
	}
}