package com.example.rentals.controller

import com.example.rentals.domain.customer.Customer
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

    private val TENANT_ID = 22
    private val customerService = mock<CustomerService>()
    private val customer = Customer("test@email.com", "John Doe", 1234567890)
    private val customerController = CustomerController(customerService)

    @Test
    fun `should return 201 if the customer is saved in the database`() {
        whenever(customerService.create(customer, TENANT_ID)).thenReturn(true.toMono())
        customerController.create(TENANT_ID, customer).subscribe {
            assertEquals(ResponseEntity<Customer>(HttpStatus.CREATED), it)
        }
    }

    @Test
    fun `should return 409 if the save customer fail`() {
        whenever(customerService.create(customer, TENANT_ID)).thenReturn(false.toMono())
        customerController.create(TENANT_ID, customer).subscribe {
            assertEquals(ResponseEntity<Customer>(HttpStatus.CONFLICT), it)
        }
    }

    @Test
    fun `should return 200 and the customer if the customer is present in the database`() {
        whenever(customerService.get(customer.email, TENANT_ID)).thenReturn(customer.toMono())
        customerController.get(TENANT_ID, customer.email).subscribe {
            assertEquals(ResponseEntity(customer, HttpStatus.OK), it)
        }
    }

    @Test
    fun `should return 404 if the customer is not present in the database`() {
        whenever(customerService.get(customer.email, TENANT_ID)).thenReturn(Mono.empty())
        val response = customerController.get(TENANT_ID, customer.email).block()!!
        assertEquals(ResponseEntity<Customer>(HttpStatus.NOT_FOUND), response)
    }

    @Test
    fun `should return 204 if the customer is patched successfully`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"contact\", \"value\": \"1234098765\"}]"

        whenever(customerService.patch(customer.email, patch, TENANT_ID)).thenReturn(true.toMono())

        customerController.patch(TENANT_ID, customer.email, patch).subscribe {
            assertEquals(ResponseEntity<Customer>(HttpStatus.NO_CONTENT), it)
        }
    }

    @Test
    fun `should return 404 if the customer patch fails`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"contact\", \"value\": \"1234098765\"}]"

        whenever(customerService.patch(customer.email, patch, TENANT_ID)).thenReturn(false.toMono())

        customerController.patch(TENANT_ID, customer.email, patch).subscribe {
            assertEquals(ResponseEntity<Customer>(HttpStatus.NOT_FOUND), it)
        }
    }
}