package com.example.rentals

import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories
class ReactiveMongoConfiguration : AbstractReactiveMongoConfiguration() {
    @Value("\${MONGODB_URI}")
    lateinit var connectionString: String

    @Value("\${DATABASENAME}")
    lateinit var database: String

    override fun reactiveMongoClient() = MongoClients.create(connectionString)

    override fun getDatabaseName() = database
}