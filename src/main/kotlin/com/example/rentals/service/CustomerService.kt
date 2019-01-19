package com.example.rentals.service

import com.example.rentals.domain.customer.Customer
import com.example.rentals.domain.customer.CustomerPrimaryKey
import com.example.rentals.domain.customer.CustomerTable
import com.example.rentals.repository.CustomerRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Service
class CustomerService(val customerRepository: CustomerRepository) {
    fun create(customer: Customer, tenantId: Int): Mono<Boolean> {
        val primaryKey = CustomerPrimaryKey(tenantId, customer.email)
        return with(customerRepository) {
            existsById(primaryKey).flatMap { it ->
                when (it) {
                    false -> save(CustomerTable(primaryKey, customer)).map { (it.customer == customer) }
                    else -> false.toMono()
                }
            }
        }
    }

    fun get(email: String, tenantId: Int): Mono<Customer> {
        val primaryKey = CustomerPrimaryKey(tenantId, email)
        return customerRepository.findById(primaryKey).map { it.customer }
    }

    fun delete(email: String, tenantId: Int): Mono<Boolean> {
        val primaryKey = CustomerPrimaryKey(tenantId, email)
        return with(customerRepository) {
            findById(primaryKey)
                    .flatMap { deleteById(primaryKey)
                            .then(true.toMono())
                    }
        }
                .defaultIfEmpty(false)
    }

    fun patch(email: String, patch: String, tenantId: Int): Mono<Boolean> {
        val primaryKey = CustomerPrimaryKey(tenantId, email)
        val mapper = ObjectMapper()
            return get(email, tenantId)
                    .flatMap { it -> mapper.readValue(
                            JsonPatch.apply(stringToJsonNode(patch),
                                    stringToJsonNode(mapper.writeValueAsString(it)))
                                    .toString(),
                            Customer::class.java
                    ).toMono()
                    }.flatMap { customerRepository.save(CustomerTable(primaryKey, it))
                            .flatMap { true.toMono() }
                            .defaultIfEmpty(false)
                    }.switchIfEmpty(false.toMono())
    }

    private fun stringToJsonNode(string: String): JsonNode = ObjectMapper().readTree(string)

    fun exists(email: String, tenantId: Int): Mono<Boolean> {
        val primaryKey = CustomerPrimaryKey(tenantId, email)
        return customerRepository.existsById(primaryKey)
    }
}