package com.example.rentals.service

import com.example.rentals.domain.customer.Customer
import com.example.rentals.domain.customer.CustomerPrimaryKey
import com.example.rentals.domain.customer.CustomerTable
import com.example.rentals.repository.CustomerRepository
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

class CustomerServiceTest {
    private val TENANT_ID = 22
    private val customerRepository = mock<CustomerRepository>()
    private val customer = Customer("test@email.com", "John Doe", 1234567890)
    private val customerPartitionKey = CustomerPrimaryKey(TENANT_ID, customer.email)
    private val customerTable = CustomerTable(customerPartitionKey, customer)

    @Test
    fun `should create a customer in the database`() {
        val customerService = CustomerService(customerRepository)

        val captor = argumentCaptor<CustomerTable> {
            whenever(customerRepository.save(capture())).thenReturn(customerTable.toMono())
        }
        whenever(customerRepository.existsById(customerPartitionKey)).thenReturn(false.toMono())

        customerService.create(customer, TENANT_ID).subscribe {
            Assert.assertEquals(customer, captor.firstValue)
        }
    }

    @Test
    fun `should return false if the customer to saved is present in the database`() {
        val customerService = CustomerService(customerRepository)

        whenever(customerRepository.existsById(customerPartitionKey)).thenReturn(true.toMono())

        customerService.create(customer, TENANT_ID).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should return true when the customer is saved`() {
        whenever(customerRepository.save(customerTable)).thenReturn(customerTable.toMono())
        whenever(customerRepository.existsById(customerPartitionKey)).thenReturn(false.toMono())

        val customerService = CustomerService(customerRepository)

        customerService.create(customer, TENANT_ID).subscribe {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `should get the customer from database if present`() {
        whenever(customerRepository.findById(customerPartitionKey)).thenReturn(customerTable.toMono())

        val customerService = CustomerService(customerRepository)

        customerService.get(customer.email, TENANT_ID).subscribe {
            Assert.assertEquals(it, customer)
        }
    }

    @Test
    fun `should delete the customer from database if present`() {
        whenever(customerRepository.findById(customerPartitionKey)).thenReturn(customerTable.toMono())
        val captor = argumentCaptor<CustomerPrimaryKey>()
        whenever(customerRepository.deleteById(customerPartitionKey))
                .thenReturn(Mono.create { it.success(null) })

        val customerService = CustomerService(customerRepository)

        customerService.delete(customer.email, TENANT_ID).subscribe {
            Assert.assertEquals(it, true)
            Assert.assertEquals(captor.lastValue, customerPartitionKey)
        }
    }

    @Test
    fun `should return false if the customer to be deleted is not present in the database`() {
        whenever(customerRepository.findById(customerPartitionKey)).thenReturn(Mono.empty())

        val customerService = CustomerService(customerRepository)

        customerService.delete(customer.email, TENANT_ID).subscribe {
            Assert.assertEquals(it, false)
        }
    }

    @Test
    fun `should return true if the customer patch is successful`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"contact\", \"value\": \"1234098765\"}]"
        val updatedCustomer = Customer("test@email.com", "John Doe", 1234098765)
        val updatedCustomerTable = CustomerTable(customerPartitionKey, updatedCustomer)
        val customerService = CustomerService(customerRepository)
        val captor = argumentCaptor<CustomerTable>()

        whenever(customerRepository.findById(customerPartitionKey)).thenReturn(updatedCustomerTable.toMono())
        whenever(customerRepository.save(captor.capture())).thenReturn(updatedCustomerTable.toMono())

        customerService.patch(customer.email, patch, TENANT_ID).subscribe {
            Assert.assertTrue(it)
            assertEquals(captor.lastValue, updatedCustomer)
        }
    }

    @Test
    fun `should return false if the customer to be patched is not found`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"contact\", \"value\": \"1234098765\"}]"
        val customerService = CustomerService(customerRepository)

        whenever(customerRepository.findById(customerPartitionKey)).thenReturn(Mono.empty())

        customerService.patch(customer.email, patch, TENANT_ID).subscribe {
            Assert.assertFalse(it)
        }
    }

    @Test
    fun `should return false if the patch fails`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"contact\", \"value\": \"1234098765\"}]"
        val customerService = CustomerService(customerRepository)
        val updatedCustomer = Customer("test@email.com", "John Doe", 1234098765)
        val updatedCustomerTable = CustomerTable(customerPartitionKey, updatedCustomer)

        whenever(customerRepository.findById(customerPartitionKey)).thenReturn(customerTable.toMono())
        whenever(customerRepository.save(updatedCustomerTable)).thenReturn(Mono.empty())

        customerService.patch(customer.email, patch, TENANT_ID).subscribe {
            Assert.assertFalse(it)
        }
    }

    @Test
    fun `should return true if the customer exists in the database`() {
        val customerService = CustomerService(customerRepository)

        whenever(customerRepository.existsById(customerPartitionKey)).thenReturn(true.toMono())

        customerService.exists(customer.email, TENANT_ID).subscribe {
            assertTrue(it)
        }
    }

    @Test
    fun `should return false if the customer does not exist in the database`() {
        val customerService = CustomerService(customerRepository)

        whenever(customerRepository.existsById(customerPartitionKey)).thenReturn(false.toMono())

        customerService.exists(customer.email, TENANT_ID).subscribe {
            assertFalse(it)
        }
    }
}