package com.example.rentals.service

import com.example.rentals.domain.Customer
import com.example.rentals.repository.CustomerRepository
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import reactor.core.publisher.toMono

class CustomerServiceTest {
    private val customerRepository = mock<CustomerRepository>()
    private val customer = Customer("test@email.com", "John Doe", "1234567890")

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
}