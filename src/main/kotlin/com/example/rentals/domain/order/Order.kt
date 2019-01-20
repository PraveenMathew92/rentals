package com.example.rentals.domain.order

import org.springframework.data.cassandra.core.mapping.UserDefinedType
import java.util.UUID
import java.util.Date

@UserDefinedType
data class Order(
    val assetId: UUID = UUID(0, 0),
    val email: String = "someone@test.com",
    val checkoutDate: Date = Date(),
    val rate: Int = 0
)