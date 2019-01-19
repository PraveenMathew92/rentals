package com.example.rentals.domain.asset

import java.util.UUID

data class Asset(
    val id: UUID = UUID(0, 0),
    val name: String = "",
    val category: CategoryFields = CategoryFields("", "", "")
)