package com.example.rentals.service

import com.example.rentals.domain.Order
import com.example.rentals.domain.OrderPrimaryKey
import com.example.rentals.exceptions.CustomerNotFoundException
import com.example.rentals.repository.OrderRepository
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID
import java.util.Date

internal class OrderServiceTest {
    val email = "email@test.com"
    val assetId = UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
    val order = Order(OrderPrimaryKey(email, assetId), Date(), 100)

    val orderRepository = mock<OrderRepository>()
    val customerService = mock<CustomerService>()

    @Test
    fun `should create an order in the database`() {
        val orderService = OrderService(orderRepository, customerService)
        val captor = argumentCaptor<Order>()

        whenever(orderRepository.existsById(OrderPrimaryKey(email, assetId)))
            .thenReturn(false.toMono())
        whenever(orderRepository.save(captor.capture())).thenReturn(order.toMono())

        orderService.create(order).subscribe {
            assertTrue(it)
            assertEquals(order, captor.lastValue)
        }
    }

    @Test
    fun `should not create the order in the database if the order already exists`() {
        val orderService = OrderService(orderRepository, customerService)

        whenever(orderRepository.existsById(OrderPrimaryKey(email, assetId)))
            .thenReturn(true.toMono())

        orderService.create(order).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should get the order form the database if present`() {
        val orderService = OrderService(orderRepository, customerService)
        val captor = argumentCaptor<OrderPrimaryKey>()

        whenever(orderRepository.findById(captor.capture()))
                .thenReturn(order.toMono())

        orderService.get(email, assetId.toString()).subscribe {
            assertEquals(OrderPrimaryKey(email, assetId), captor.lastValue)
        }
    }

    @Test
    fun `should return true if the patch is successful`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]"
        val updatedOrder = order.copy(rate = 1000)
        val orderService = OrderService(orderRepository, customerService)
        val captor = argumentCaptor<Order>()

        whenever(orderRepository.findById(OrderPrimaryKey(email, assetId))).thenReturn(order.toMono())
        whenever(orderRepository.save(captor.capture())).thenReturn(updatedOrder.toMono())

        orderService.patch(email, assetId.toString(), patch).subscribe {
            assertTrue(it)
            assertEquals(updatedOrder, captor.lastValue)
        }
    }

    @Test
    fun `should return false if the patch is fails`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]"
        val updatedOrder = order.copy(rate = 1000)
        val orderService = OrderService(orderRepository, customerService)
        val captor = argumentCaptor<Order>()

        whenever(orderRepository.findById(OrderPrimaryKey(email, assetId))).thenReturn(order.toMono())
        whenever(orderRepository.save(updatedOrder)).thenReturn(order.toMono())

        orderService.patch(email, assetId.toString(), patch).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should return false if the order is not found in the database`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]"
        val orderService = OrderService(orderRepository, customerService)

        whenever(orderRepository.findById(OrderPrimaryKey(email, assetId))).thenReturn(Mono.empty())

        orderService.patch(email, assetId.toString(), patch).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should return true if the order is deleted from the database`() {
        val orderService = OrderService(orderRepository, customerService)

        whenever(orderRepository.existsById(OrderPrimaryKey(email, assetId))).thenReturn(true.toMono())
        whenever(orderRepository.deleteById(OrderPrimaryKey(email, assetId))).thenReturn(Mono.create { it.success(null) })

        orderService.delete(email, assetId.toString()).subscribe {
            assertTrue(it)
        }
    }

    @Test
    fun `should return false if the order is not present in the database`() {
        val orderService = OrderService(orderRepository, customerService)

        whenever(orderRepository.existsById(OrderPrimaryKey(email, assetId))).thenReturn(false.toMono())

        orderService.delete(email, assetId.toString()).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should return false if the order is not deleted database`() {
        val orderService = OrderService(orderRepository, customerService)

        whenever(orderRepository.existsById(OrderPrimaryKey(email, assetId))).thenReturn(false.toMono())
        whenever(orderRepository.deleteById(OrderPrimaryKey(email, assetId))).thenReturn(Mono.empty())

        orderService.delete(email, assetId.toString()).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should  throw CustomerNotFoundException for a non existent customer`() {
        val orderService = OrderService(orderRepository, customerService)

        whenever(customerService.exists(email)).thenReturn(false.toMono())

        assertThrows<CustomerNotFoundException> { orderService.create(order).block() }
    }
}