package com.example.rentals.converter

import com.example.rentals.domain.asset.CategoryFields
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CategoryToJSONConverterTest {
    @Test
    fun `should convert the Json to an Object of CategoryField`() {
        val json = "{\"maker\":\"The Producer\",\"type\":\"The Genera\",\"size\":\"Duration\"}"
        val categoryDetails = CategoryFields("The Producer", "The Genera", "Duration")

        assertEquals(json, CategoryToJSONConverter().convert(categoryDetails))
    }
}