package com.example.rentals.service

import com.example.rentals.domain.Customer
import com.example.rentals.repository.CustomerRepository
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
}