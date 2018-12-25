package com.example.rentals.controller

import com.example.rentals.domain.OrderPrimaryKey
import com.example.rentals.domain.Order
import com.example.rentals.service.OrderService
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID
import java.util.Date

class OrderControllerTest {
    val email = "test@email.com"
    val assetId = UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
    private val order = Order(OrderPrimaryKey(email,
            assetId), Date(), 1000)
    private val orderService = mock<OrderService>()

    @Test
    fun `should should return 201 if the order is created`() {
        val orderController = OrderController(orderService)
        whenever(orderService.create(order)).thenReturn(true.toMono())

        orderController.create(order).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.CREATED), it)
        }
    }

    @Test
    fun `should should return 409 if the order fails to be created`() {
        val orderController = OrderController(orderService)
        whenever(orderService.create(order)).thenReturn(false.toMono())

        orderController.create(order).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.CONFLICT), it)
        }
    }

    @Test
    fun `should should return 200 if the order is fetched from the database`() {
        val orderController = OrderController(orderService)
        whenever(orderService.get(email, assetId.toString())).thenReturn(order.toMono())

        orderController.get(email, assetId.toString()).subscribe {
            assertEquals(HttpStatus.OK, it.statusCode)
            assertEquals(order, it.body)
        }
    }

    @Test
    fun `should should return 404 if the order is not found in the database`() {
        val orderController = OrderController(orderService)
        whenever(orderService.get(email, assetId.toString())).thenReturn(Mono.empty())

        orderController.get(email, assetId.toString()).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.NOT_FOUND), it)
        }
    }

    @Test
    fun `should should return 204 if the order is deleted form the database`() {
        val orderController = OrderController(orderService)
        whenever(orderService.delete(email, assetId.toString())).thenReturn(true.toMono())

        orderController.delete(email, assetId.toString()).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.NO_CONTENT), it)
        }
    }

    @Test
    fun `should should return 404 if the order is deleted form the database`() {
        val orderController = OrderController(orderService)
        whenever(orderService.delete(email, assetId.toString())).thenReturn(false.toMono())

        orderController.delete(email, assetId.toString()).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.NOT_FOUND), it)
        }
    }

    @Test
    fun `should should return 204 if the order is updated`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]"
        val orderController = OrderController(orderService)
        whenever(orderService.patch(email, assetId.toString(), patch)).thenReturn(true.toMono())

        orderController.patch(email, assetId.toString(), patch).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.NO_CONTENT), it)
        }
    }

    @Test
    fun `should should return 404 if the order fails to update`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]"
        val orderController = OrderController(orderService)
        whenever(orderService.patch(email, assetId.toString(), patch)).thenReturn(false.toMono())

        orderController.patch(email, assetId.toString(), patch).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.NOT_FOUND), it)
        }
    }
}