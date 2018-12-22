package com.example.rentals.controller

import com.example.rentals.domain.Order
import com.example.rentals.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

class OrderController(val orderService: OrderService){
    fun create(order: Order): Mono<ResponseEntity<Order>> {
        return orderService.create(order).map { when (it) {
            true -> ResponseEntity<Order>(HttpStatus.CREATED)
            else -> ResponseEntity<Order>(HttpStatus.CONFLICT)
        } }
    }
}