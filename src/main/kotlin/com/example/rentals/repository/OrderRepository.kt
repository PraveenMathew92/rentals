package com.example.rentals.repository

import com.example.rentals.domain.order.OrderPrimaryKey
import com.example.rentals.domain.order.OrderTable
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import org.springframework.data.cassandra.repository.Query
import reactor.core.publisher.Flux
import java.util.UUID

@Repository
interface OrderRepository : ReactiveCassandraRepository<OrderTable, OrderPrimaryKey> {
    @Query("SELECT * FROM asset_orders WHERE primarykey = " +
            "{ partitionkey = { \'tenantId\': ?0, \'email\': ?1 } } ;")
    fun findByKeyEmail(email: String, tenantId: Int): Flux<OrderTable>

    @Query("SELECT * FROM asset_orders WHERE assetid = ?0 ALLOW FILTERING;")
    fun findByKeyAssetId(assetId: UUID): Flux<OrderTable>
}
