package com.example.rentals.controller

import com.example.rentals.domain.customer.Customer
import com.example.rentals.service.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import javax.validation.Valid

@RestController
@RequestMapping("/customer")
class CustomerController(val customerService: CustomerService) {
    @PostMapping
    fun create(@RequestHeader tenantId: Int, @Valid @RequestBody customer: Customer): Mono<ResponseEntity<Customer>> {
        return customerService.create(customer, tenantId).map { when (it) {
            true -> ResponseEntity<Customer>(HttpStatus.CREATED)
            else -> ResponseEntity<Customer>(HttpStatus.CONFLICT)
        } }
    }

    @GetMapping("/{email}")
    fun get(@RequestHeader tenantId: Int, @PathVariable email: String): Mono<ResponseEntity<Customer>> {
        return customerService.get(email, tenantId)
                .map { ResponseEntity(it, HttpStatus.OK) }
                .switchIfEmpty(ResponseEntity<Customer>(HttpStatus.NOT_FOUND).toMono())
    }

    @PatchMapping("/{email}")
    fun patch(@RequestHeader tenantId: Int, @PathVariable email: String, @RequestBody patch: String): Mono<ResponseEntity<Customer>> {
        return customerService.patch(email, patch, tenantId).flatMap { when (it) {
            true -> ResponseEntity<Customer>(HttpStatus.NO_CONTENT).toMono()
            else -> ResponseEntity<Customer>(HttpStatus.NOT_FOUND).toMono()
        } }
    }
}