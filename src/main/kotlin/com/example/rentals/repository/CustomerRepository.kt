package com.example.rentals.repository

import com.example.rentals.domain.Customer
import com.example.rentals.domain.CustomerPrimaryKey
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : ReactiveCassandraRepository<Customer, CustomerPrimaryKey>