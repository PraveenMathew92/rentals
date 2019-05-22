package com.example.rentals.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document
data class Asset(
    @Id val id: UUID = UUID(0, 0),
    val name: String = "",
    val category: CategoryFields = CategoryFields("", "", "")
)