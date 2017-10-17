package com.lukasz.plawny.onetimetoken.dbconfig;

import java.util.ArrayList;
import java.util.List;

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
public class CassandraConfig extends AbstractCassandraConfiguration{
	
	private static final String KEYSPACE = "one_time_token_keyspace";
    private static final String USERNAME = "cassandra";
    private static final String PASSWORD = "cassandra";
    private static final String NODES = "127.0.0.1";
    
    @Bean
    @Override
    public CassandraCqlClusterFactoryBean cluster() {
        CassandraCqlClusterFactoryBean bean = new CassandraCqlClusterFactoryBean();
        bean.setKeyspaceCreations(getKeyspaceCreations());
        bean.setContactPoints(NODES);
        bean.setUsername(USERNAME);
        bean.setPassword(PASSWORD);
        return bean;
    }
	
	private CreateKeyspaceSpecification getKeySpaceSpecification() {
        CreateKeyspaceSpecification oneTimeTokenKeyspace = new CreateKeyspaceSpecification();
        DataCenterReplication dcr = new DataCenterReplication("dc1", 1);
        oneTimeTokenKeyspace.name(KEYSPACE);
        oneTimeTokenKeyspace.ifNotExists(true).createKeyspace().withNetworkReplication(dcr);
        return oneTimeTokenKeyspace;
    }
	
    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"com.lukasz.plawny.onetimetoken.dto"};
    }

	@Override
	protected String getKeyspaceName() {
		return KEYSPACE;
	}
	
	@Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }
	
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        List<CreateKeyspaceSpecification> createKeyspaceSpecifications = new ArrayList<>();
        createKeyspaceSpecifications.add(getKeySpaceSpecification());
        return createKeyspaceSpecifications;
    }
}