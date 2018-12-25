package com.example.rentals.service

import com.example.rentals.domain.Order
import com.example.rentals.domain.OrderPrimaryKey
import com.example.rentals.repository.OrderRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID

@Service
class OrderService(val orderRepository: OrderRepository) {
    fun create(order: Order): Mono<Boolean> {
            return orderRepository.existsById(order.id).flatMap {
            when (it) {
                false -> orderRepository.save(order).map { it == order }
                else -> false.toMono()
            }
        }
    }

    fun get(email: String, id: String): Mono<Order> {
        val orderPrimaryKey = OrderPrimaryKey(email, UUID.fromString(id))
        return orderRepository.findById(orderPrimaryKey)
    }

    fun patch(email: String, id: String, patch: String): Mono<Boolean> {
        val mapper = ObjectMapper()
        return get(email, id)
                .flatMap { mapper.readValue(
                        JsonPatch.apply(stringToJsonNode(patch),
                                stringToJsonNode(mapper.writeValueAsString(it)))
                                .toString(),
                        Order::class.java
                ).toMono()
                }.flatMap { updatedOrder -> orderRepository.save(updatedOrder)
                        .map { it == updatedOrder }
                        .defaultIfEmpty(false)
                }.switchIfEmpty(false.toMono())
    }

    private fun stringToJsonNode(string: String): JsonNode = ObjectMapper().readTree(string)

    fun delete(email: String, assetId: String): Mono<Boolean> {
        val orderPrimaryKey = OrderPrimaryKey(email, UUID.fromString(assetId))
        return orderRepository.existsById(orderPrimaryKey).flatMap { when (it) {
                true -> orderRepository.deleteById(orderPrimaryKey).map { true }
                else -> false.toMono()
            }
        }
    }
}