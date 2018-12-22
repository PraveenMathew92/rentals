package com.example.rentals.service

import com.example.rentals.domain.Asset
import com.example.rentals.domain.Customer
import com.example.rentals.domain.Order
import com.example.rentals.domain.OrderPrimaryKey
import com.example.rentals.repository.OrderRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Service
class OrderService(val orderRepository: OrderRepository){
    fun create(order: Order): Mono<Boolean> {
            return orderRepository.existsById(order.id).flatMap {
            when (it) {
                false -> orderRepository.save(order).map { it == order }
                else -> false.toMono()
            }
        }
    }

    fun get(customer: Customer, asset: Asset): Mono<Order> {
        val orderId = OrderPrimaryKey(customer, asset)
        return orderRepository.findById(orderId)
    }
}