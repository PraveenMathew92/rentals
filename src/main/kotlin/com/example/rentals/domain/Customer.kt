package com.example.rentals.domain

import org.springframework.data.cassandra.core.mapping.PrimaryKey

class Customer(@PrimaryKey val email: String, val name: String, val contact: Int)