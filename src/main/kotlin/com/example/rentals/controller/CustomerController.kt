package com.example.rentals.controller

import com.example.rentals.domain.Customer
import com.example.rentals.service.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
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

    @GetMapping("/{email}")
    fun get(email: String): Mono<ResponseEntity<Customer>> {
        return customerService.get(email)
                .map { ResponseEntity(it, HttpStatus.OK) }
                .switchIfEmpty(ResponseEntity<Customer>(HttpStatus.NOT_FOUND).toMono())
    }

    @DeleteMapping("/{email}")
    fun delete(email: String): Mono<ResponseEntity<Customer>> {
        return customerService.delete(email)
                .flatMap { when (it) {
                    true -> ResponseEntity<Customer>(HttpStatus.NO_CONTENT).toMono()
                    else -> ResponseEntity<Customer>(HttpStatus.NOT_FOUND).toMono()
                } }
    }

    @PatchMapping("/{email}")
    fun patch(email: String, patch: String): Mono<ResponseEntity<Customer>> {
        return customerService.patch(email,patch).flatMap { when (it) {
            true -> ResponseEntity<Customer>(HttpStatus.NO_CONTENT).toMono()
            else -> ResponseEntity<Customer>(HttpStatus.NOT_FOUND).toMono()
        } }
    }
}