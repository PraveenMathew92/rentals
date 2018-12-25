package com.example.rentals.domain

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable
import java.util.UUID

@PrimaryKeyClass
data class OrderPrimaryKey(
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED) val email: String = "someone@example.com",
    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED) val assetId: UUID = UUID(0, 0)
) : Serializable
