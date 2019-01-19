package com.example.rentals.repository

import com.example.rentals.domain.customer.CustomerTable
import com.example.rentals.domain.customer.CustomerPrimaryKey
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : ReactiveCassandraRepository<CustomerTable, CustomerPrimaryKey>