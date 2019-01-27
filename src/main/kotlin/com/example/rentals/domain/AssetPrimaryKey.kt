package com.example.rentals.domain

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable
import java.util.UUID

@PrimaryKeyClass
data class AssetPrimaryKey(
        @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1) val tenantId: String = "",
        @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2) val uuid: UUID = UUID(0, 0)
): Serializable