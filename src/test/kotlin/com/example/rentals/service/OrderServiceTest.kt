package com.example.rentals.service

import com.example.rentals.domain.Asset
import com.example.rentals.domain.CategoryFields
import com.example.rentals.domain.Customer
import com.example.rentals.domain.Order
import com.example.rentals.repository.OrderRepository
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID
import java.util.Date

internal class OrderServiceTest{
    @Test
    fun `should create an order in the database`() {
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Asset Name", CategoryFields())
        val customer = Customer("email@test.com", "TestUser", 1234567890)
        val order = Order(UUID.fromString("752f3c7c-f449-4ea4-85e1-ad61dd2dbf53"), customer, asset, Date(), 100)

        val orderRepository = mock<OrderRepository>()
        val orderService = OrderService(orderRepository)
        val captor = argumentCaptor<Order>()

        whenever(orderRepository.findById(UUID.fromString("752f3c7c-f449-4ea4-85e1-ad61dd2dbf53")))
            .thenReturn(Mono.empty())
        whenever(orderRepository.save(captor.capture())).thenReturn(order.toMono())

        orderService.save(order).subscribe {
            assertTrue(it)
            assertEquals(order, captor.lastValue)
        }
    }
}