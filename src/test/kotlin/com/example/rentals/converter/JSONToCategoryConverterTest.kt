package com.example.rentals.converter

import com.example.rentals.domain.asset.CategoryFields
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException

internal class JSONToCategoryConverterTest {
    @Test
    fun `should convert the Json to an Object of CategoryField`() {
        val json = " {\"maker\":\"The Producer\",\"type\":\"The Genera\",\"size\":\"Duration\"}"
        val categoryDetails = CategoryFields("The Producer", "The Genera", "Duration")

        assertEquals(categoryDetails, JSONToCategoryConverter().convert(json))
    }

    @Test
    fun `should throw IllegalStateException in case of Exception`() {
        val json = " {\"maker\":\"The Producer type\":\"The Genera\",\"size\":\"Duration\"}"
        assertThrows<IllegalStateException> { JSONToCategoryConverter().convert(json) }
    }
}