package com.example.rentals.controller

import com.example.rentals.domain.Customer
import com.example.rentals.service.CustomerService
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

class CustomerControllerTest {

    private val customerService = mock<CustomerService>()
    private val customer = Customer("test@email.com", "John Doe", 1234567890)
    private val customerController = CustomerController(customerService)

    @Test
    fun `should return 200 if the customer is saved in the database`() {
        whenever(customerService.create(customer)).thenReturn(true.toMono())
        customerController.create(customer).subscribe {
            assertEquals(ResponseEntity<Customer>(HttpStatus.OK), it)
        }
    }

    @Test
    fun `should return 409 if the save customer fail`() {
        whenever(customerService.create(customer)).thenReturn(false.toMono())
        customerController.create(customer).subscribe {
            assertEquals(ResponseEntity<Customer>(HttpStatus.CONFLICT), it)
        }
    }

    @Test
    fun `should return 200 and the customer if the customer is present in the database`() {
        whenever(customerService.get(customer.email)).thenReturn(customer.toMono())
        customerController.get(customer.email).subscribe {
            assertEquals(ResponseEntity(customer, HttpStatus.OK), it)
        }
    }

    @Test
    fun `should return 404 if the customer is not present in the database`() {
        whenever(customerService.get(customer.email)).thenReturn(Mono.empty())
        val response = customerController.get(customer.email).block()!!
        assertEquals(ResponseEntity<Customer>(HttpStatus.NOT_FOUND), response)
    }

    @Test
    fun `should return 204 if the customer is deleted from the database`() {
        whenever(customerService.delete(customer.email)).thenReturn(true.toMono())
        customerController.delete(customer.email).subscribe {
            assertEquals(ResponseEntity<Customer>(HttpStatus.NO_CONTENT), it)
        }
    }

    @Test
    fun `should return 404 if the customer to be deleted is not found in the database`() {
        whenever(customerService.delete(customer.email)).thenReturn(false.toMono())
        customerController.delete(customer.email).subscribe {
            assertEquals(ResponseEntity<Customer>(HttpStatus.NOT_FOUND), it)
        }
    }
}