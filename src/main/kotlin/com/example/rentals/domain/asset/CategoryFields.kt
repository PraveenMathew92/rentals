package com.example.rentals.domain.asset

import org.springframework.data.cassandra.core.mapping.UserDefinedType

@UserDefinedType
data class CategoryFields(val maker: String = "", val type: String = "", val size: String = "")