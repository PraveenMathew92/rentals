package com.example.rentals.service

import com.example.rentals.domain.order.Order
import com.example.rentals.domain.order.OrderPartitionKey
import com.example.rentals.domain.order.OrderPrimaryKey
import com.example.rentals.domain.order.OrderTable
import com.example.rentals.exceptions.AssetCannotBeRentedException
import com.example.rentals.exceptions.AssetNotFoundException
import com.example.rentals.exceptions.CustomerCannotBeDeletedException
import com.example.rentals.exceptions.CustomerNotFoundException
import com.example.rentals.exceptions.AssetCannotBeDeletedException
import com.example.rentals.repository.OrderRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID

@Service
class OrderService(val orderRepository: OrderRepository, val customerService: CustomerService, val assetService: AssetService) {
    fun create(order: Order, tenantId: Int): Mono<Boolean> {
        val doesCustomerExists = customerService.exists(order.email, tenantId)
        val doesAssetExists = assetService.exists(order.assetId, tenantId)
        val orderId = OrderPrimaryKey(OrderPartitionKey(tenantId, order.email))
        val canAssetBeRented = orderRepository.findByKeyAssetId(order.assetId)
                .next()
                .map { false }
                .switchIfEmpty(true.toMono())

        return Mono.zip(doesCustomerExists, doesAssetExists, canAssetBeRented).flatMap {
            when {
                it.t1.not() -> throw CustomerNotFoundException()
                it.t2.not() -> throw AssetNotFoundException()
                it.t3.not() -> throw AssetCannotBeRentedException()
                else -> {
                    orderRepository.existsById(orderId).flatMap { doesOrderExists ->
                        when (doesOrderExists) {
                            false -> orderRepository.save(OrderTable(orderId, order)).map { it.order == order }
                            else -> false.toMono()
                        }
                    }
                }
            }
        }
    }

    fun get(email: String, assetId: String, tenantId: Int): Mono<Order> {
        val orderPrimaryKey = OrderPrimaryKey(OrderPartitionKey(tenantId, email), UUID.fromString(assetId))
        return orderRepository.findById(orderPrimaryKey).map { it.order }
    }

    fun patch(email: String, assetId: String, patch: String, tenantId: Int): Mono<Boolean> {
        val primaryKey = OrderPrimaryKey(OrderPartitionKey(tenantId, email), UUID.fromString(assetId))
        val mapper = ObjectMapper()
        return get(email, assetId, tenantId)
                .flatMap { mapper.readValue(
                        JsonPatch.apply(stringToJsonNode(patch),
                                stringToJsonNode(mapper.writeValueAsString(it)))
                                .toString(),
                        Order::class.java
                ).toMono()
                }.flatMap { updatedOrder -> orderRepository.save(OrderTable(primaryKey, updatedOrder))
                        .map { it.order == updatedOrder }
                        .defaultIfEmpty(false)
                }.switchIfEmpty(false.toMono())
    }

    private fun stringToJsonNode(string: String): JsonNode = ObjectMapper().readTree(string)

    fun delete(email: String, assetId: String, tenantId: Int): Mono<Boolean> {
        val orderPrimaryKey = OrderPrimaryKey(OrderPartitionKey(tenantId, email), UUID.fromString(assetId))
        return orderRepository.existsById(orderPrimaryKey).flatMap { when (it) {
                true -> orderRepository.deleteById(orderPrimaryKey).then(true.toMono())
                else -> false.toMono()
            }
        }
    }

    fun safeDeleteCustomer(email: String, tenantId: Int): Mono<Boolean> {
        val canDeleteCustomer = orderRepository.findByKeyEmail(email, tenantId)
                .next()
                .map { false }
                .switchIfEmpty(true.toMono())
        return canDeleteCustomer.flatMap { when (it) {
            true -> customerService.delete(email, tenantId)
            else -> throw CustomerCannotBeDeletedException()
        } }
    }

    fun safeDeleteAsset(assetId: UUID, tenantId: Int): Mono<Boolean> {
        val canDeleteAsset = orderRepository.findByKeyAssetId(assetId)
                .next()
                .map { false }
                .switchIfEmpty(true.toMono())
        return canDeleteAsset.flatMap { when (it) {
            true -> assetService.delete(assetId.toString(), tenantId)
            else -> throw AssetCannotBeDeletedException()
        } }
    }
}