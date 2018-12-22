package com.example.rentals.service

import com.example.rentals.domain.*
import com.example.rentals.repository.OrderRepository
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import reactor.core.publisher.toMono
import java.util.*

internal class OrderServiceTest{
    val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
            "Asset Name", CategoryFields())
    val customer = Customer("email@test.com", "TestUser", 1234567890)
    val order = Order(OrderPrimaryKey(customer, asset), Date(), 100)

    val orderRepository = mock<OrderRepository>()

    @Test
    fun `should create an order in the database`() {
        val orderService = OrderService(orderRepository)
        val captor = argumentCaptor<Order>()

        whenever(orderRepository.existsById(OrderPrimaryKey(customer, asset)))
            .thenReturn(false.toMono())
        whenever(orderRepository.save(captor.capture())).thenReturn(order.toMono())

        orderService.create(order).subscribe {
            assertTrue(it)
            assertEquals(order, captor.lastValue)
        }
    }

    @Test
    fun `should not create the order in the database if the order already exists`() {
        val orderService = OrderService(orderRepository)

        whenever(orderRepository.existsById(OrderPrimaryKey(customer, asset)))
            .thenReturn(true.toMono())

        orderService.create(order).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should get the order form the database if present`(){
        val orderService = OrderService(orderRepository)
        val captor = argumentCaptor<OrderPrimaryKey>()

        whenever(orderRepository.findById(captor.capture()))
                .thenReturn(order.toMono())

        orderService.get(customer, asset).subscribe{
            assertEquals(OrderPrimaryKey(customer, asset), captor.lastValue)
        }
    }
}