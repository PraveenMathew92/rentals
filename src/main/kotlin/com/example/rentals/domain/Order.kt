package com.example.rentals.domain

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.Date

@Table
data class Order(@PrimaryKey val id: OrderPrimaryKey = OrderPrimaryKey(), val checkoutDate: Date = Date(), val rate: Int = 0)