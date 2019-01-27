package com.example.rentals.domain

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.UUID

@Table
data class Asset(
    @PrimaryKey val id: AssetPrimaryKey = AssetPrimaryKey(),
    val name: String = "",
    val category: CategoryFields = CategoryFields("", "", "")
)