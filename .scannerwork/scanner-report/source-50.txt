package com.example.rentals.domain

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import javax.validation.constraints.Email

@Table
data class Customer(@PrimaryKey @field:Email val email: String = "someone@example.com", val name: String = "", val contact: Int = 0)