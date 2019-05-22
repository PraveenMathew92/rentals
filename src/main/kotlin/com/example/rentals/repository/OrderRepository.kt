package com.example.rentals.repository

import com.example.rentals.domain.Order
import com.example.rentals.domain.OrderPrimaryKey
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.UUID

@Repository
interface OrderRepository : ReactiveMongoRepository<Order, OrderPrimaryKey> {
    fun findById_Email(email: String): Flux<Order>

    fun findById_AssetId(assetId: UUID): Flux<Order>
}
