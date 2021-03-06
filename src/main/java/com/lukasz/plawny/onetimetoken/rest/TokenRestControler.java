package com.lukasz.plawny.onetimetoken.rest;

import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lukasz.plawny.onetimetoken.dto.Token;
import com.lukasz.plawny.onetimetoken.service.TokenService;

/**
 * 
 * This class provides REST interface to create and use token
 *
 */
@RestController
public class TokenRestControler {

	@Autowired
	private TokenService tokenService;

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenRestControler.class);

	/**
	 * Create new token for specified url
	 * 
	 * @param url
	 * @param response
	 *            used to set appropriate http status code
	 * @return token
	 */
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public String createToken(@RequestParam URL url, HttpServletResponse response) {
		Token token = tokenService.createToken(url);
		response.setStatus(HttpServletResponse.SC_CREATED);
		return token.getTokenId();
	}

	/**
	 * Search for token and redirect to url related to the token
	 * 
	 * @param tokenId
	 * @param response
	 *            used to set appropriate http status code
	 */
	@RequestMapping(value = "/token/{tokenId}", method = RequestMethod.GET)
	public void useToken(@PathVariable String tokenId, HttpServletResponse response) {
		Token token = tokenService.findToken(tokenId);
		if (token == null) {
			LOGGER.info("Token not found: " + tokenId);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else {
			LOGGER.info("Token found: " + tokenId + ". Redirect to " + token.getUrl());
			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", token.getUrl().toString());
		}
	}
}