package com.lukasz.plawny.onetimetoken.dbconfig;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.config.CassandraCqlClusterFactoryBean;
import org.springframework.cassandra.config.DataCenterReplication;
import org.springframework.cassandra.core.keyspace.CreateKeyspaceSpecification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig extends AbstractCassandraConfiguration {

	private static final String KEYSPACE = "one_time_token_keyspace";

	@Value("${spring.data.cassandra.username}")
	private String username;
	@Value("${spring.data.cassandra.password}")
	private String password;
	@Value("${spring.data.cassandra.contact-points}")
	private String nodes;
	@Value("${spring.data.cassandra.port}")
	private int port;

	@Bean
	@Override
	public CassandraCqlClusterFactoryBean cluster() {
		CassandraCqlClusterFactoryBean bean = new CassandraCqlClusterFactoryBean();
		bean.setKeyspaceCreations(getKeyspaceCreations());
		bean.setContactPoints(nodes);
		bean.setUsername(username);
		bean.setPassword(password);
		bean.setPort(port);
		return bean;
	}

	@Override
	protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
		List<CreateKeyspaceSpecification> createKeyspaceSpecifications = new ArrayList<>();
		createKeyspaceSpecifications.add(getKeyspaceSpecification());
		return createKeyspaceSpecifications;
	}

	@Override
	public String[] getEntityBasePackages() {
		return new String[] { "com.lukasz.plawny.onetimetoken.dto" };
	}

	@Override
	protected String getKeyspaceName() {
		return KEYSPACE;
	}

	@Override
	public SchemaAction getSchemaAction() {
		return SchemaAction.CREATE_IF_NOT_EXISTS;
	}

	@Override
	protected int getPort() {
		return port;
	}

	private CreateKeyspaceSpecification getKeyspaceSpecification() {
		CreateKeyspaceSpecification oneTimeTokenKeyspace = new CreateKeyspaceSpecification();
		DataCenterReplication dcr = new DataCenterReplication("dc1", 1);
		oneTimeTokenKeyspace.name(KEYSPACE);
		oneTimeTokenKeyspace.ifNotExists(true).createKeyspace().withNetworkReplication(dcr);
		return oneTimeTokenKeyspace;
	}

}