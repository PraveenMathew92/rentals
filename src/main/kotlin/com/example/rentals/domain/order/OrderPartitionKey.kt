package com.example.rentals.domain.order

import org.springframework.data.cassandra.core.mapping.UserDefinedType

@UserDefinedType
data class OrderPartitionKey(val tenantId: Int = 0, val email: String = "")