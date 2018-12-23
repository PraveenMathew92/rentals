package com.example.rentals.domain

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable

@PrimaryKeyClass
data class OrderPrimaryKey(
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED) val customer: Customer = Customer(),
    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED) val asset: Asset = Asset()
) : Serializable
