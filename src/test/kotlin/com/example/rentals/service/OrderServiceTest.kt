package com.example.rentals.service

import com.example.rentals.domain.order.Order
import com.example.rentals.domain.order.OrderPartitionKey
import com.example.rentals.domain.order.OrderPrimaryKey
import com.example.rentals.domain.order.OrderTable
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
    private val email = "email@test.com"
    private val assetId = UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
    private val order = Order(assetId, email, Date(), 100)
    private val tenantId = 22
    private val orderTable = OrderTable(OrderPrimaryKey(OrderPartitionKey(tenantId, order.email), assetId), order)

    private val orderRepository = mock<OrderRepository>()
    private val customerService = mock<CustomerService>()
    private val assetService = mock<AssetService>()

    @Test
    fun `should create an order in the database`() {
        val orderService = OrderService(orderRepository, customerService, assetService)
        val captor = argumentCaptor<OrderTable>()

        whenever(orderRepository.existsById(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId)))
            .thenReturn(false.toMono())
        whenever(orderRepository.save(captor.capture())).thenReturn(orderTable.toMono())
        whenever(customerService.exists(email, tenantId)).thenReturn(true.toMono())
        whenever(assetService.exists(assetId)).thenReturn(true.toMono())
        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.empty())

        orderService.create(order, tenantId).subscribe {
            assertTrue(it)
            assertEquals(order, captor.lastValue.order)
        }
    }

    @Test
    fun `should not create the order in the database if the order already exists`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.existsById(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId)))
            .thenReturn(true.toMono())
        whenever(customerService.exists(email, tenantId)).thenReturn(true.toMono())
        whenever(assetService.exists(assetId)).thenReturn(true.toMono())
        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.empty())

        orderService.create(order, tenantId).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should get the order form the database if present`() {
        val orderService = OrderService(orderRepository, customerService, assetService)
        val captor = argumentCaptor<OrderPrimaryKey>()

        whenever(orderRepository.findById(captor.capture()))
                .thenReturn(orderTable.toMono())

        orderService.get(email, assetId.toString(), tenantId).subscribe {
            assertEquals(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId), captor.lastValue)
        }
    }

    @Test
    fun `should return true if the patch is successful`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]"
        val updatedOrderTable = orderTable.copy(order = order.copy(rate = 1000))

        val orderService = OrderService(orderRepository, customerService, assetService)
        val captor = argumentCaptor<OrderTable>()

        whenever(orderRepository.findById(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId)))
                .thenReturn(orderTable.toMono())
        whenever(orderRepository.save(captor.capture())).thenReturn(updatedOrderTable.toMono())

        orderService.patch(email, assetId.toString(), patch, tenantId).subscribe {
            assertTrue(it)
            assertEquals(updatedOrderTable.order, captor.lastValue.order)
        }
    }

    @Test
    fun `should return false if the patch is fails`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]"
        val updatedOrderTable = orderTable.copy(order = order.copy(rate = 1000))
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.findById(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId)))
                .thenReturn(orderTable.toMono())
        whenever(orderRepository.save(updatedOrderTable)).thenReturn(orderTable.toMono())

        orderService.patch(email, assetId.toString(), patch, tenantId).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should return false if the order is not found in the database`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]"
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.findById(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId)))
                .thenReturn(Mono.empty())

        orderService.patch(email, assetId.toString(), patch, tenantId).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should return true if the order is deleted from the database`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.existsById(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId)))
                .thenReturn(true.toMono())
        whenever(orderRepository.deleteById(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId)))
                .thenReturn(Mono.create { it.success(null) })

        orderService.delete(email, assetId.toString(), tenantId).subscribe {
            assertTrue(it)
        }
    }

    @Test
    fun `should return false if the order is not present in the database`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.existsById(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId)))
                .thenReturn(false.toMono())

        orderService.delete(email, assetId.toString(), tenantId).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should return false if the order is not deleted database`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.existsById(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId)))
                .thenReturn(false.toMono())
        whenever(orderRepository.deleteById(OrderPrimaryKey(OrderPartitionKey(tenantId, email), assetId)))
                .thenReturn(Mono.empty())

        orderService.delete(email, assetId.toString(), tenantId).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should  throw CustomerNotFoundException for a non existent customer`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(customerService.exists(email, tenantId)).thenReturn(false.toMono())
        whenever(assetService.exists(assetId)).thenReturn(true.toMono())
        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.empty())

        assertThrows<CustomerNotFoundException> { orderService.create(order, tenantId).block() }
    }

    @Test
    fun `should  throw AssetNotFoundException for a non existent asset`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(customerService.exists(email, tenantId)).thenReturn(true.toMono())
        whenever(assetService.exists(assetId)).thenReturn(false.toMono())
        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.empty())

        assertThrows<AssetNotFoundException> { orderService.create(order, tenantId).block() }
    }

    @Test
    fun `should throw AssetCannotBeRentedException if the asset is already rented by another customer`() {
        val orderService = OrderService(orderRepository, customerService, assetService)
        val anotherOrder = orderTable.copy(order = order.copy(rate = 1000))

        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.just(anotherOrder))
        whenever(customerService.exists(email, tenantId)).thenReturn(true.toMono())
        whenever(assetService.exists(assetId)).thenReturn(true.toMono())

        assertThrows<AssetCannotBeRentedException> { orderService.create(order, tenantId).block() }
    }

    @Test
    fun `should throw CustomerCannotBeDeletedException on deleting the customer if the customer has rented out an asset`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.findByKeyEmail(email, tenantId)).thenReturn(Flux.just(orderTable))

        assertThrows<CustomerCannotBeDeletedException> { orderService.safeDeleteCustomer(email, tenantId).block() }
    }

    @Test
    fun `should call the delete of customer service if the customer is safe to be deleted`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.findByKeyEmail(email, tenantId)).thenReturn(Flux.empty())
        whenever(customerService.delete(email, tenantId)).thenReturn(true.toMono())

        orderService.safeDeleteCustomer(email, tenantId).subscribe {
            verify(customerService, times(1)).delete(email, tenantId)
        }
    }

    @Test
    fun `should throw AssetCannotBeDeletedException on deleting the asset if the asset is rented out`() {
        val orderService = OrderService(orderRepository, customerService, assetService)

        whenever(orderRepository.findByKeyAssetId(assetId)).thenReturn(Flux.just(orderTable))

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