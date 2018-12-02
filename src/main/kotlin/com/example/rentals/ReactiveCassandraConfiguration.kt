package com.example.rentals

import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories


@Configuration
@EnableReactiveCassandraRepositories
class ReactiveCassandraConfiguration: AbstractReactiveCassandraConfiguration() {
    override fun getKeyspaceName(): String = "rentals"
}