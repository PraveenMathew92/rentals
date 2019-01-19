package com.example.rentals.converter

import com.example.rentals.domain.order.OrderPrimaryKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

internal class JSONToOrderPrimaryKeyConverterTest {
    @Test
    fun `should convert the Json to an Object of CategoryField`() {
        val json = "{\"email\":\"email@test.com\",\"assetId\":\"65cf3c7c-f449-4cd4-85e1-bc61dd2db64e\"}"
        val orderPrimaryKey = OrderPrimaryKey("email@test.com", UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"))

        assertEquals(orderPrimaryKey, JSONToOrderPrimaryKeyConverter().convert(json))
    }

    @Test
    fun `should throw IllegalStateException in case of Exception`() {
        val json = "{\"email\":\"incorrect-email.com\",\"assetId\":\"65cf3c7c4cd4-85e1-bc61dd2db64e\"}"
        val exception = org.junit.jupiter.api.assertThrows<IllegalStateException> {
            JSONToOrderPrimaryKeyConverter().convert(json)
        }
    }
}