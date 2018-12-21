package com.example.rentals.domain

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern

@Table
class Customer(
        @PrimaryKey @field:Email(message = "Enter a valid email") val email: String,
        val name: String,
        @field:Pattern(regexp = "\\d{10}", message = "Contact should be of ten digits") val contact: String
)