package com.example.rentals.repository

import com.example.rentals.domain.Customer
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : ReactiveMongoRepository<Customer, String>