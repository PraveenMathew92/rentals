package com.example.rentals.service

import com.example.rentals.domain.Order
import com.example.rentals.repository.OrderRepository
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

class OrderService(val orderRepository: OrderRepository){
    fun save(order: Order): Mono<Boolean> {
        orderRepository.save(order)
        return true.toMono()
    }
}