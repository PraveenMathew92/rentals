package com.example.rentals.domain

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import java.util.Date
import java.util.UUID

data class Order(@PrimaryKey val id: UUID, val customer: Customer, val asset: Asset, val checkoutDate : Date, val rate: Int)