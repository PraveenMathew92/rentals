package com.example.rentals.converter

import com.example.rentals.domain.OrderPrimaryKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

internal class OrderPrimaryKeyToStringConverterTest {
    @Test
    fun `should convert the Object of OrderPrimaryKey to a JSON`() {
        val json = "{\"email\":\"email@test.com\",\"assetId\":\"65cf3c7c-f449-4cd4-85e1-bc61dd2db64e\"}"
        val orderPrimaryKey = OrderPrimaryKey("email@test.com", UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"))

        assertEquals(json, OrderPrimaryKeyToJSONConverter().convert(orderPrimaryKey))
    }
}