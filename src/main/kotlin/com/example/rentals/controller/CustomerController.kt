package com.example.rentals.controller

import com.example.rentals.domain.Customer
import com.example.rentals.service.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import javax.validation.Valid

@RestController("/customer")
class CustomerController(val customerService: CustomerService) {
    @PostMapping
    fun create(@Valid @RequestBody customer: Customer): Mono<ResponseEntity<Customer>> {
        return customerService.create(customer).map { when (it) {
                true -> ResponseEntity<Customer>(HttpStatus.OK)
                else -> ResponseEntity<Customer>(HttpStatus.CONFLICT)
            }
        }
    }

    fun get(email: String): Mono<ResponseEntity<Customer>> {
        return customerService.get(email)
                .map { ResponseEntity(it, HttpStatus.OK) }
                .switchIfEmpty(ResponseEntity<Customer>(HttpStatus.NOT_FOUND).toMono())
    }
}