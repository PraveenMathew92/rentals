package com.example.rentals

import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories
class ReactiveMongoConfiguration : AbstractReactiveMongoConfiguration() {
    override fun reactiveMongoClient() = MongoClients.create()

    override fun getDatabaseName() = "rentals"
}