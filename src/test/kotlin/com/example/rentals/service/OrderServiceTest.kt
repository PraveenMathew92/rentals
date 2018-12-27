package com.example.rentals.service

import com.example.rentals.domain.Order
import com.example.rentals.domain.OrderPrimaryKey
import com.example.rentals.exceptions.AssetCannotBeDeletedException
import com.example.rentals.exceptions.CustomerCannotBeDeletedException
import com.example.rentals.exceptions.CustomerNotFoundException
import com.example.rentals.exceptions.AssetNotFoundException
import com.example.rentals.exceptions.AssetCannotBeRentedException
import com.example.rentals.repository.OrderRepository
import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.argumentCaptor
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
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
    val assetService = mock<AssetService>()

    @Test
    fun `should create an order in the database`() {
        val orderService = OrderService(orderRepository, customerService, assetService)
        val captor = argumentCaptor<Order>()

        whenever(orderRepository.existsById(OrderPrimaryKey(email, assetId)))
            .thenReturn(false.toMono())
        whenever(orderRepository.save(captor.capture())).thenReturn(order.toMono())
        whenever(customerService.exists(email)).thenReturn(true.toMono())
        whenever(assetService.exists(assetId)).thenReturn(true.toMono())
        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.empty())

        orderService.create(order).subscribe {
            assertTrue(it)
            assertEquals(order, captor.lastValue)
        }
    }

    @Test
    fun `should not create the order in the database if the order already exists`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.existsById(OrderPrimaryKey(email, assetId)))
            .thenReturn(true.toMono())
        whenever(customerService.exists(email)).thenReturn(true.toMono())
        whenever(assetService.exists(assetId)).thenReturn(true.toMono())
        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.empty())

        orderService.create(order).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should get the order form the database if present`() {
        val orderService = OrderService(orderRepository, customerService, assetService)
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
        val orderService = OrderService(orderRepository, customerService, assetService)
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
        val orderService = OrderService(orderRepository, customerService, assetService)
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
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.findById(OrderPrimaryKey(email, assetId))).thenReturn(Mono.empty())

        orderService.patch(email, assetId.toString(), patch).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should return true if the order is deleted from the database`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.existsById(OrderPrimaryKey(email, assetId))).thenReturn(true.toMono())
        whenever(orderRepository.deleteById(OrderPrimaryKey(email, assetId))).thenReturn(Mono.create { it.success(null) })

        orderService.delete(email, assetId.toString()).subscribe {
            assertTrue(it)
        }
    }

    @Test
    fun `should return false if the order is not present in the database`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.existsById(OrderPrimaryKey(email, assetId))).thenReturn(false.toMono())

        orderService.delete(email, assetId.toString()).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should return false if the order is not deleted database`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.existsById(OrderPrimaryKey(email, assetId))).thenReturn(false.toMono())
        whenever(orderRepository.deleteById(OrderPrimaryKey(email, assetId))).thenReturn(Mono.empty())

        orderService.delete(email, assetId.toString()).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should  throw CustomerNotFoundException for a non existent customer`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(customerService.exists(email)).thenReturn(false.toMono())
        whenever(assetService.exists(assetId)).thenReturn(true.toMono())
        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.empty())

        assertThrows<CustomerNotFoundException> { orderService.create(order).block() }
    }

    @Test
    fun `should  throw AssetNotFoundException for a non existent asset`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(customerService.exists(email)).thenReturn(true.toMono())
        whenever(assetService.exists(assetId)).thenReturn(false.toMono())
        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.empty())

        assertThrows<AssetNotFoundException> { orderService.create(order).block() }
    }

    @Test
    fun `should throw AssetCannotBeRentedException if the asset is already rented by another customer`() {
        val orderService = OrderService(orderRepository, customerService, assetService)
        val anotherOrderId = order.id.copy(email = "another-test@email.com")
        val anotherOrder = order.copy(id = anotherOrderId)

        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.just(anotherOrder))
        whenever(customerService.exists(email)).thenReturn(true.toMono())
        whenever(assetService.exists(assetId)).thenReturn(true.toMono())

        assertThrows<AssetCannotBeRentedException> { orderService.create(order).block() }
    }

    @Test
    fun `should throw CustomerCannotBeDeletedException on deleting the customer if the customer has rented out an asset`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.findByKeyEmail(email)).thenReturn(Flux.just(order))

        assertThrows<CustomerCannotBeDeletedException> { orderService.safeDeleteCustomer(email).block() }
    }

    @Test
    fun `should call the delete of customer service if the customer is safe to be deleted`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.findByKeyEmail(email)).thenReturn(Flux.empty())
        whenever(customerService.delete(email)).thenReturn(true.toMono())

        orderService.safeDeleteCustomer(email).subscribe {
            verify(customerService, times(1)).delete(email)
        }
    }

    @Test
    fun `should throw AssetCannotBeDeletedException on deleting the asset if the asset is rented out`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.just(order))

        assertThrows<AssetCannotBeDeletedException> { orderService.safeDeleteAsset(assetId).block() }
    }

    @Test
    fun `should call the delete of asset service if the customer is safe to be deleted`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.empty())
        whenever(assetService.delete(assetId.toString())).thenReturn(true.toMono())

        orderService.safeDeleteAsset(assetId).subscribe {
            verify(assetService, times(1)).delete(assetId.toString())
        }
    }
}