package com.example.rentals.repository

import com.example.rentals.domain.Order
import com.example.rentals.domain.OrderPrimaryKey
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: ReactiveCassandraRepository<Order, OrderPrimaryKey>
