package com.example.rentals

import com.example.rentals.converter.CategoryToJSONConverter
import com.example.rentals.converter.JSONToCategoryConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories
import org.springframework.data.convert.CustomConversions

@Configuration
@EnableReactiveCassandraRepositories
class ReactiveCassandraConfiguration : AbstractReactiveCassandraConfiguration() {
    private val KEYSPACE_NAME = "rentals"

    @Value("\${cassandra.host}")
    private lateinit var CASSANDRA_HOST: String

    @Value("\${cassandra.port}")
    private lateinit var CASSANDRA_PORT: String

    override fun getKeyspaceName(): String = KEYSPACE_NAME

    override fun customConversions(): CustomConversions {
        return CustomConversions(
                CustomConversions.StoreConversions.NONE,
                listOf(JSONToCategoryConverter(), CategoryToJSONConverter())
        )
    }

    override fun cluster(): CassandraClusterFactoryBean {
        val cluster = super.cluster()
        cluster.setContactPoints(CASSANDRA_HOST)
        cluster.setPort(CASSANDRA_PORT.toInt())
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