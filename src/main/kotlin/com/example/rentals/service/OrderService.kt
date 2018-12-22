package com.example.rentals.service

import com.example.rentals.domain.Order
import com.example.rentals.domain.OrderPrimaryKey
import com.example.rentals.repository.OrderRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID

@Service
class OrderService(val orderRepository: OrderRepository, val customerService: CustomerService, val assetService: AssetService) {
    fun create(order: Order): Mono<Boolean> {
            return orderRepository.existsById(order.id).flatMap {
            when (it) {
                false -> orderRepository.save(order).map { it == order }
                else -> false.toMono()
            }
        }
    }

    fun get(email: String, id: String): Mono<Order> {
        return Mono.zip(customerService.get(email), assetService.get(id)).flatMap {
                val orderPrimaryKey = OrderPrimaryKey(it.t1, it.t2)
                orderRepository.findById(orderPrimaryKey)
        }
    }

    fun patch(email: String, id: UUID, patch: String): Mono<Boolean> {
TODO()
    }
}