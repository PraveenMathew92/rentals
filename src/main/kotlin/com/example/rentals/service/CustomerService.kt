package com.example.rentals.service

import com.example.rentals.domain.Customer
import com.example.rentals.repository.CustomerRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Service
class CustomerService(val customerRepository: CustomerRepository) {
    fun create(customer: Customer): Mono<Boolean> {
        return with(customerRepository) {
            existsById(customer.email).flatMap { it ->
                when (it) {
                    false -> save(customer).map { (it == customer) }
                    else -> false.toMono()
                }
            }
        }
    }

    fun get(email: String): Mono<Customer> {
        return customerRepository.findById(email)
    }

    fun delete(email: String): Mono<Boolean> {
        return with(customerRepository) {
            findById(email)
                    .flatMap { deleteById(email)
                            .then(true.toMono())
                    }
        }
                .defaultIfEmpty(false)
    }

    fun patch(email: String, patch: String): Mono<Boolean> {
        val mapper = ObjectMapper()
            return get(email)
                    .flatMap { it -> mapper.readValue(
                            JsonPatch.apply(stringToJsonNode(patch),
                                    stringToJsonNode(mapper.writeValueAsString(it)))
                                    .toString(),
                            Customer::class.java
                    ).toMono()
                    }.flatMap { customerRepository.save(it)
                            .flatMap { true.toMono() }
                            .defaultIfEmpty(false)
                    }.switchIfEmpty(false.toMono())
    }

    private fun stringToJsonNode(string: String): JsonNode = ObjectMapper().readTree(string)
}