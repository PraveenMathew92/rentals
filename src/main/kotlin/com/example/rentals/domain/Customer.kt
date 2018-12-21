package com.example.rentals.domain

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import javax.validation.constraints.Email

@Table
class Customer(@PrimaryKey @field:Email val email: String, val name: String, val contact: Int)