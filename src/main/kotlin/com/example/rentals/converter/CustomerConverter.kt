package com.example.rentals.converter

import com.example.rentals.domain.customer.Customer
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import java.io.IOException
import java.lang.IllegalStateException

class JSONToCustomerConverter : Converter<String, Customer> {
    override fun convert(source: String): Customer {
        try {
            return ObjectMapper().readValue(source, Customer::class.java)
        } catch (exception: IOException) {
            throw IllegalStateException(exception)
        }
    }
}

class CustomerToJSONConverter : Converter<Customer, String> {
    override fun convert(source: Customer): String =
            ObjectMapper().writeValueAsString(source)
}