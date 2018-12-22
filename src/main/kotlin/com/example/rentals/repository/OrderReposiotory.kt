package com.example.rentals.repository

import com.example.rentals.domain.Order
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderRepository: ReactiveCassandraRepository<Order, UUID>
