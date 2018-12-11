package com.example.rentals.controller

import com.example.rentals.domain.Customer
import com.example.rentals.service.CustomerService
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.toMono

class CustomerControllerTest{

    private val customerService = mock<CustomerService>()
    private val customer = Customer("test@email.com", "John Doe", 1234567890)
    private val customerController = CustomerController(customerService)

    @Test
    fun `should return 200 if the customer is saved in the database`() {
        whenever(customerService.create(customer)).thenReturn(true.toMono())
        customerController.create(customer).subscribe{
            assertEquals(ResponseEntity<Customer>(HttpStatus.OK), it)
        }
    }

    @Test
    fun `should return 409 if the save customer fail`() {
        whenever(customerService.create(customer)).thenReturn(false.toMono())
        customerController.create(customer).subscribe{
            assertEquals(ResponseEntity<Customer>(HttpStatus.CONFLICT), it)
        }
    }
}