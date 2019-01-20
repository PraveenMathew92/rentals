package com.example.rentals.domain.customer

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table(value = "customer")
class CustomerTable(
    @PrimaryKey val primaryKey: CustomerPrimaryKey = CustomerPrimaryKey(),
    val customer: Customer = Customer()
)