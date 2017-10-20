package com.lukasz.plawny.onetimetoken.dao;

import static org.junit.Assert.*;
import static com.lukasz.plawny.onetimetoken.testutil.OneTimeTokenTestUtility.*;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cassandra.core.WriteOptions;
import org.springframework.data.cassandra.core.CassandraOperations;

import com.lukasz.plawny.onetimetoken.dto.Token;

@RunWith(MockitoJUnitRunner.class)
public class TokenDaoImplTest {

	@Mock
	private CassandraOperations cassandraOperations;

	private TokenDao tokenDao;
	private Token predefinedToken;

	@Before
	public void setUp() throws MalformedURLException {
		tokenDao = new TokenDaoImpl(20);
		Whitebox.setInternalState(tokenDao, "cassandraOperations", cassandraOperations);
		predefinedToken = createPredefinedTokenForGoogleUrl();
		Mockito.when(cassandraOperations.insert(Mockito.eq(predefinedToken), Mockito.any(WriteOptions.class)))
				.thenReturn(predefinedToken);
		Mockito.when(cassandraOperations.selectOneById(Token.class, INVALID_TOKEN_ID)).thenReturn(null);
		Mockito.when(cassandraOperations.selectOneById(Token.class, VALID_TOKEN_ID)).thenReturn(predefinedToken);
	}

	@Test
	public void create_ShouldSaveToken() throws MalformedURLException {
		Token savedToken = tokenDao.create(predefinedToken);
		Mockito.verify(cassandraOperations, Mockito.times(1)).insert(Mockito.eq(predefinedToken),
				Mockito.any(WriteOptions.class));
		assertEquals(predefinedToken, savedToken);
	}

	@Test
	public void find_ShouldReturnSavedToken() throws MalformedURLException {
		Token token = tokenDao.find(VALID_TOKEN_ID);
		assertEquals(predefinedToken, token);
	}

	@Test
	public void find_ShouldReturnNull_IfNoTokenWithSpecifiedId() {
		Token token = tokenDao.find("invalidId");
		assertNull(token);
	}

}