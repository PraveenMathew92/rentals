package com.example.rentals.domain

import java.io.Serializable
import java.util.UUID

data class OrderPrimaryKey(
    val email: String = "",
    val assetId: UUID = UUID(0, 0)
) : Serializable
