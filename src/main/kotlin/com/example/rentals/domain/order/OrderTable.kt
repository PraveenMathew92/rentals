package com.example.rentals.domain.order

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table(value = "asset_orders")
class OrderTable(
    @PrimaryKey val primaryKey: OrderPrimaryKey = OrderPrimaryKey(),
    val order: Order = Order()
)