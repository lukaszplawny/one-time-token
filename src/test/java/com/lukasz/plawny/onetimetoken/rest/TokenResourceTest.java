package com.lukasz.plawny.onetimetoken.rest;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.lukasz.plawny.onetimetoken.dto.Token;
import com.lukasz.plawny.onetimetoken.service.TokenService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TokenRestControler.class)
public class TokenResourceTest {

	@MockBean
	private TokenService tokenService;

	@Autowired
	private MockMvc mockMvc;

	private URL url;
	private Token token;

	@Before
	public void init() throws MalformedURLException {
		url = new URL("http://www.google.com");
		token = new Token();
		token.setUrl(url);
		token.setTokenId("sampleid1");
		Mockito.when(tokenService.generateToken(url)).thenReturn(token);
		Mockito.when(tokenService.findToken("invalidtoken")).thenReturn(null);
		Mockito.when(tokenService.findToken(token.getTokenId())).thenReturn(token);
	}

	@Test
	public void shouldCreateNewToken() throws Exception {
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/token").param("url", token.getUrl().toString()))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
		assertEquals(token.getTokenId(), result.getResponse().getContentAsString());
	}

	@Test
	public void shouldReturnNotFound_WhenTokenIsInvalid() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/token/invalidtoken"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void shouldRedirectToUrl_WhenTokenIsValid() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/token/" + token.getTokenId()))
				.andExpect(MockMvcResultMatchers.redirectedUrl(token.getUrl().toString()));
	}
	
	//TODO test with MalformedUrlException

}