package com.example.rentals.controller

import com.example.rentals.domain.*
import com.example.rentals.service.OrderService
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.toMono
import java.util.UUID
import java.util.Date

class OrderControllerTest{
    private val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
            "Swift Dzire",
            CategoryFields("Maruti Suzuki", "Lxi", "5 Seater"))
    private val customer = Customer("test@email.com", "John Doe", 1234567890)
    private val order = Order(OrderPrimaryKey(customer, asset), Date(), 1000)
    private val orderService = mock<OrderService>()

    @Test
    fun `should should return 201 if the order is created`() {
        val orderController = OrderController(orderService)
        whenever(orderService.create(order)).thenReturn(true.toMono())

        orderController.create(order).subscribe{
            assertEquals(ResponseEntity<Order>(HttpStatus.CREATED), it)
        }
    }

    @Test
    fun `should should return 409 if the order fails to be created`() {
        val orderController = OrderController(orderService)
        whenever(orderService.create(order)).thenReturn(false.toMono())

        orderController.create(order).subscribe{
            assertEquals(ResponseEntity<Order>(HttpStatus.CONFLICT), it)
        }
    }
}