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
public class TokenRestControlerTest {

	@MockBean
	private TokenService tokenService;

	@Autowired
	private MockMvc mockMvc;

	private URL url;
	private Token predefinedToken;

	@Before
	public void createPredefinedTokenAndMockTokenService() throws MalformedURLException {
		url = new URL("http://www.google.com");
		predefinedToken = new Token();
		predefinedToken.setUrl(url);
		predefinedToken.setTokenId("sampleid1");
		Mockito.when(tokenService.createToken(url)).thenReturn(predefinedToken);
		Mockito.when(tokenService.findToken("invalidtoken")).thenReturn(null);
		Mockito.when(tokenService.findToken(predefinedToken.getTokenId())).thenReturn(predefinedToken);
	}

	@Test
	public void createToken_shouldReturnNewToken() throws Exception {
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/token").param("url", predefinedToken.getUrl().toString()))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
		assertEquals(predefinedToken.getTokenId(), result.getResponse().getContentAsString());
	}

	@Test
	public void useToken_ShouldReturnNotFound_WhenTokenIsInvalid() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/token/invalidtoken"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void useToken_ShouldRedirectToUrl_WhenTokenIsValid() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/token/" + predefinedToken.getTokenId()))
				.andExpect(MockMvcResultMatchers.redirectedUrl(predefinedToken.getUrl().toString()));
	}
	
	//TODO test with MalformedUrlException

}