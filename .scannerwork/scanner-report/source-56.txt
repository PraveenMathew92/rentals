package com.example.rentals.domain

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.UUID
import java.util.Date

@Table(value = "asset_orders")
data class Order(@PrimaryKey val id: OrderPrimaryKey = OrderPrimaryKey("", UUID(0, 0)), val checkoutDate: Date = Date(), val rate: Int = 0)