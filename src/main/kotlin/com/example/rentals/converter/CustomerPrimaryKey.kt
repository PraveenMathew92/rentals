package com.example.rentals.converter

import com.example.rentals.domain.customer.CustomerPrimaryKey
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import java.io.IOException
import java.lang.IllegalStateException

class JSONToCustomerPrimaryKeyConverter : Converter<String, CustomerPrimaryKey> {
    override fun convert(source: String): CustomerPrimaryKey {
        try {
            return ObjectMapper().readValue(source, CustomerPrimaryKey::class.java)
        } catch (exception: IOException) {
            throw IllegalStateException(exception)
        }
    }
}

class CustomerPrimaryKeyToJSONConverter : Converter<CustomerPrimaryKey, String> {
    override fun convert(source: CustomerPrimaryKey): String =
            ObjectMapper().writeValueAsString(source)
}