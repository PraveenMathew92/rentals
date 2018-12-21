package com.example.rentals.service

import com.example.rentals.domain.Customer
import com.example.rentals.repository.CustomerRepository
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

class CustomerServiceTest {
    private val customerRepository = mock<CustomerRepository>()
    private val customer = Customer("test@email.com", "John Doe", 1234567890)

    @Test
    fun `should create a customer in the database`() {
        val customerService = CustomerService(customerRepository)

        val captor = argumentCaptor<Customer> {
            whenever(customerRepository.save(capture())).thenReturn(customer.toMono())
        }
        whenever(customerRepository.existsById(customer.email)).thenReturn(false.toMono())

        customerService.create(customer).subscribe {
            Assert.assertEquals(customer, captor.firstValue)
        }
    }

    @Test
    fun `should return false if the customer to saved is present in the database`() {
        val customerService = CustomerService(customerRepository)

        whenever(customerRepository.existsById(customer.email)).thenReturn(true.toMono())

        customerService.create(customer).subscribe {
            assertFalse(it)
        }
    }

    @Test
    fun `should return true when the customer is saved`() {
        whenever(customerRepository.save(customer)).thenReturn(customer.toMono())
        whenever(customerRepository.existsById(customer.email)).thenReturn(false.toMono())

        val customerService = CustomerService(customerRepository)

        customerService.create(customer).subscribe {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `should get the customer from database if present`() {
        whenever(customerRepository.findById(customer.email)).thenReturn(customer.toMono())

        val customerService = CustomerService(customerRepository)

        customerService.get(customer.email).subscribe {
            Assert.assertEquals(it, customer)
        }
    }

    @Test
    fun `should delete the customer from database if present`() {
        whenever(customerRepository.findById(customer.email)).thenReturn(customer.toMono())
        val captor = argumentCaptor<String>()
        whenever(customerRepository.deleteById(captor.capture())).thenReturn(Mono.create { it.success(null) })

        val customerService = CustomerService(customerRepository)

        customerService.delete(customer.email).subscribe {
            Assert.assertEquals(it, true)
            Assert.assertEquals(captor.lastValue, customer.email)
        }
    }

    @Test
    fun `should return false if the customer to be deleted is not present in the database`() {
        whenever(customerRepository.findById(customer.email)).thenReturn(Mono.empty())

        val customerService = CustomerService(customerRepository)

        customerService.delete(customer.email).subscribe {
            Assert.assertEquals(it, false)
        }
    }

    @Test
    fun `should return true if the customer patch is successful`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"contact\", \"value\": \"1234098765\"}]"
        val updatedCustomer = Customer("test@email.com", "John Doe", 1234098765)
        val customerService = CustomerService(customerRepository)
        val captor = argumentCaptor<Customer>()

        whenever(customerRepository.findById(customer.email)).thenReturn(customer.toMono())
        whenever(customerRepository.save(captor.capture())).thenReturn(updatedCustomer.toMono())

        customerService.patch(customer.email, patch).map {
            Assert.assertTrue(it)
            assertEquals(captor.lastValue, updatedCustomer)
        }
    }

    @Test
    fun `should return false if the customer to be patched is not found`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"contact\", \"value\": \"1234098765\"}]"
        val customerService = CustomerService(customerRepository)

        whenever(customerRepository.findById(customer.email)).thenReturn(Mono.empty())

        customerService.patch(customer.email, patch).map {
            Assert.assertFalse(it)
        }
    }

    @Test
    fun `should return false if the patch fails`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"contact\", \"value\": \"1234098765\"}]"
        val customerService = CustomerService(customerRepository)
        val updatedCustomer = Customer("test@email.com", "John Doe", 1234098765)

        whenever(customerRepository.findById(customer.email)).thenReturn(customer.toMono())
        whenever(customerRepository.save(updatedCustomer)).thenReturn(updatedCustomer.toMono())

        customerService.patch(customer.email, patch).map {
            Assert.assertFalse(it)
        }
    }
}