package com.example.rentals.domain.customer

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import javax.validation.constraints.Email

data class Customer(@PrimaryKey @field:Email val email: String = "someone@example.com", val name: String = "", val contact: Int = 0)