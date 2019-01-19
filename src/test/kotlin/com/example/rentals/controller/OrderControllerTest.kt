package com.example.rentals.controller

import com.example.rentals.domain.asset.Asset
import com.example.rentals.domain.customer.Customer
import com.example.rentals.domain.order.Order
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
    private val email = "test@email.com"
    private val assetId = UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
    private val tenantId = 22
    private val order = Order(assetId, email, Date(), 1000)
    private val orderService = mock<OrderService>()

    @Test
    fun `should should return 201 if the order is created`() {
        val orderController = OrderController(orderService)
        whenever(orderService.create(order, tenantId)).thenReturn(true.toMono())

        orderController.create(tenantId, order).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.CREATED), it)
        }
    }

    @Test
    fun `should should return 409 if the order fails to be created`() {
        val orderController = OrderController(orderService)
        whenever(orderService.create(order, tenantId)).thenReturn(false.toMono())

        orderController.create(tenantId, order).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.CONFLICT), it)
        }
    }

    @Test
    fun `should should return 200 if the order is fetched from the database`() {
        val orderController = OrderController(orderService)
        whenever(orderService.get(email, assetId.toString(), tenantId)).thenReturn(order.toMono())

        orderController.get(tenantId, email, assetId.toString()).subscribe {
            assertEquals(HttpStatus.OK, it.statusCode)
            assertEquals(order, it.body)
        }
    }

    @Test
    fun `should should return 404 if the order is not found in the database`() {
        val orderController = OrderController(orderService)
        whenever(orderService.get(email, assetId.toString(), tenantId)).thenReturn(Mono.empty())

        orderController.get(tenantId, email, assetId.toString()).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.NOT_FOUND), it)
        }
    }

    @Test
    fun `should should return 204 if the order is deleted form the database`() {
        val orderController = OrderController(orderService)
        whenever(orderService.delete(email, assetId.toString(), tenantId)).thenReturn(true.toMono())

        orderController.delete(tenantId, email, assetId.toString()).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.NO_CONTENT), it)
        }
    }

    @Test
    fun `should should return 404 if the order is deleted form the database`() {
        val orderController = OrderController(orderService)
        whenever(orderService.delete(email, assetId.toString(), tenantId)).thenReturn(false.toMono())

        orderController.delete(tenantId, email, assetId.toString()).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.NOT_FOUND), it)
        }
    }

    @Test
    fun `should should return 204 if the order is updated`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]"
        val orderController = OrderController(orderService)
        whenever(orderService.patch(email, assetId.toString(), patch, tenantId)).thenReturn(true.toMono())

        orderController.patch(tenantId, email, assetId.toString(), patch).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.NO_CONTENT), it)
        }
    }

    @Test
    fun `should should return 404 if the order fails to update`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]"
        val orderController = OrderController(orderService)
        whenever(orderService.patch(email, assetId.toString(), patch, tenantId)).thenReturn(false.toMono())

        orderController.patch(tenantId, email, assetId.toString(), patch).subscribe {
            assertEquals(ResponseEntity<Order>(HttpStatus.NOT_FOUND), it)
        }
    }

    @Test
    fun `should return 204 if the customer is deleted from the database`() {
        val orderController = OrderController(orderService)

        whenever(orderService.safeDeleteCustomer(email, tenantId)).thenReturn(true.toMono())

        orderController.safeDeleteCustomer(tenantId, email).subscribe {
            assertEquals(ResponseEntity<Customer>(HttpStatus.NO_CONTENT), it)
        }
    }

    @Test
    fun `should return 404 if the customer to be deleted is not found in the database`() {
        val orderController = OrderController(orderService)

        whenever(orderService.safeDeleteCustomer(email, tenantId)).thenReturn(false.toMono())

        orderController.safeDeleteCustomer(tenantId, email).subscribe {
            assertEquals(ResponseEntity<Customer>(HttpStatus.NOT_FOUND), it)
        }
    }

    @Test
    fun `should return the status 204 when the delete is successful`() {
        val orderController = OrderController(orderService)

        whenever(orderService.safeDeleteAsset(assetId, tenantId)).thenReturn(true.toMono())

        orderController.safeDeleteAsset(tenantId, assetId).subscribe {
            assertEquals(ResponseEntity<Asset>(HttpStatus.NO_CONTENT), it)
        }
    }

    @Test
    fun `should return the status 404 when the delete is unsuccessful`() {
        val orderController = OrderController(orderService)

        whenever(orderService.safeDeleteAsset(assetId, tenantId)).thenReturn(false.toMono())

        orderController.safeDeleteAsset(tenantId, assetId).subscribe {
            assertEquals(ResponseEntity<Asset>(HttpStatus.NOT_FOUND), it)
        }
    }
}