package com.example.rentals.domain.asset

import org.springframework.data.cassandra.core.mapping.UserDefinedType
import java.util.UUID

@UserDefinedType
data class Asset(
    val id: UUID = UUID(0, 0),
    val name: String = "",
    val category: CategoryFields = CategoryFields("", "", "")
)