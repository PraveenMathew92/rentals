package com.example.rentals

import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@Configuration
@EnableReactiveCassandraRepositories
class ReactiveCassandraConfiguration : AbstractReactiveCassandraConfiguration() {
    private val KEYSPACE_NAME = "rentals"

    override fun getKeyspaceName(): String = KEYSPACE_NAME

    override fun cluster(): CassandraClusterFactoryBean {
        val cluster = super.cluster()
        cluster.setJmxReportingEnabled(false)
        return cluster
    }

    override fun getKeyspaceCreations(): MutableList<CreateKeyspaceSpecification> {
        return mutableListOf(CreateKeyspaceSpecification
                .createKeyspace(KEYSPACE_NAME)
                .withSimpleReplication(1)
                .ifNotExists())
    }

    override fun getSchemaAction() = SchemaAction.CREATE_IF_NOT_EXISTS
}