package com.example.rentals.converter

import com.example.rentals.domain.order.Order
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import java.io.IOException
import java.lang.IllegalStateException

class JSONToOrderConverter : Converter<String, Order> {
    override fun convert(source: String): Order {
        try {
            return ObjectMapper().readValue(source, Order::class.java)
        } catch (exception: IOException) {
            throw IllegalStateException(exception)
        }
    }
}

class OrderToJSONConverter : Converter<Order, String> {
    override fun convert(source: Order): String =
            ObjectMapper().writeValueAsString(source)
}