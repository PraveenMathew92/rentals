package com.example.rentals.converter

import com.example.rentals.domain.order.OrderPrimaryKey
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import java.io.IOException
import java.lang.IllegalStateException

class OrderPrimaryKeyToJSONConverter : Converter<OrderPrimaryKey, String> {
    override fun convert(source: OrderPrimaryKey): String =
        ObjectMapper().writeValueAsString(source)
}

class JSONToOrderPrimaryKeyConverter : Converter<String, OrderPrimaryKey> {
    override fun convert(source: String): OrderPrimaryKey {
        try {
            return ObjectMapper().readValue(source, OrderPrimaryKey::class.java)
        } catch (exception: IOException) {
            throw IllegalStateException(exception)
        }
    }
}