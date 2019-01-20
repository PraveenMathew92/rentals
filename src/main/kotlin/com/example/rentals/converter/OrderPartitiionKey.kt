package com.example.rentals.converter

import com.example.rentals.domain.order.OrderPartitionKey
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import java.io.IOException
import java.lang.IllegalStateException

class JSONToOrderPartitionKeyConverter : Converter<String, OrderPartitionKey> {
    override fun convert(source: String): OrderPartitionKey {
        try {
            return ObjectMapper().readValue(source, OrderPartitionKey::class.java)
        } catch (exception: IOException) {
            throw IllegalStateException(exception)
        }
    }
}

class OrderPartitionKeyToJSONConverter : Converter<OrderPartitionKey, String> {
    override fun convert(source: OrderPartitionKey): String =
            ObjectMapper().writeValueAsString(source)
}