package com.lukasz.plawny.onetimetoken.rest;

import static com.lukasz.plawny.onetimetoken.testutil.OneTimeTokenTestUtility.*;
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

	private Token predefinedToken;

	@Before
	public void setUp() throws MalformedURLException {
		predefinedToken = createPredefinedTokenForGoogleUrl();
		URL url = new URL(GOOGLE_URL);
		Mockito.when(tokenService.createToken(url)).thenReturn(predefinedToken);
		Mockito.when(tokenService.findToken(INVALID_TOKEN_ID)).thenReturn(null);
		Mockito.when(tokenService.findToken(VALID_TOKEN_ID)).thenReturn(predefinedToken);
	}

	@Test
	public void createToken_shouldReturnNewToken() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(TOKEN_REST_ENDPOINT).param("url", GOOGLE_URL))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
		assertEquals(VALID_TOKEN_ID, result.getResponse().getContentAsString());
	}

	@Test
	public void useToken_ShouldReturnNotFound_WhenTokenIsInvalid() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(TOKEN_REST_ENDPOINT + "/" + INVALID_TOKEN_ID))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void useToken_ShouldRedirectToUrl_WhenTokenIsValid() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(TOKEN_REST_ENDPOINT + "/" + VALID_TOKEN_ID))
				.andExpect(MockMvcResultMatchers.redirectedUrl(GOOGLE_URL));
	}

	@Test
	public void createToken_ShouldReturnBadRequest_WhenUrlParameterIsMissed() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(TOKEN_REST_ENDPOINT))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void createToken_ShouldReturnBadRequest_WhenUrlIsInvalid() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(TOKEN_REST_ENDPOINT).param("url", INVALID_URL))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
}