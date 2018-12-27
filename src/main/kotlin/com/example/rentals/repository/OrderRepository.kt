package com.example.rentals.repository

import com.example.rentals.domain.Order
import com.example.rentals.domain.OrderPrimaryKey
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import org.springframework.data.cassandra.repository.Query
import reactor.core.publisher.Flux
import java.util.UUID


@Repository
interface OrderRepository : ReactiveCassandraRepository<Order, OrderPrimaryKey>{
    @Query("SELECT * FROM asset_orders WHERE email = ?0;")
    fun findByKeyEmail(email: String): Flux<Order>

    @Query("SELECT * FROM asset_orders WHERE assetid = ?0 ALLOW FILTERING;")
    fun findByKeyAssetId(assetId: UUID): Flux<Order>
}
