package com.example.rentals.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID
import java.util.Date

@Document(collection = "asset_orders")
data class Order(@Id val id: OrderPrimaryKey = OrderPrimaryKey("", UUID(0, 0)), val checkoutDate: Date = Date(), val rate: Int = 0)