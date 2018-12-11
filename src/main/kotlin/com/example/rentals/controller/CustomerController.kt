package com.example.rentals.controller

import com.example.rentals.domain.Customer
import com.example.rentals.repository.CustomerRepository
import com.example.rentals.service.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@RestController
class CustomerController(val customerService: CustomerService) {
    fun create(customer: Customer): Mono<ResponseEntity<Customer>> {
        return customerService.create(customer).map{ when (it) {
                true -> ResponseEntity<Customer>(HttpStatus.OK)
                else -> ResponseEntity<Customer>(HttpStatus.CONFLICT)
            }
        }
    }
}