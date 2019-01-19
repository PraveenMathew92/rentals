package com.example.rentals.converter

import com.example.rentals.domain.asset.CategoryFields
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import java.io.IOException
import java.lang.IllegalStateException

class JSONToCategoryConverter : Converter<String, CategoryFields> {
    override fun convert(source: String): CategoryFields {
        try {
            return ObjectMapper().readValue(source, CategoryFields::class.java)
        } catch (exception: IOException) {
            throw IllegalStateException(exception)
        }
    }
}

class CategoryToJSONConverter : Converter<CategoryFields, String> {
    override fun convert(source: CategoryFields): String =
            ObjectMapper().writeValueAsString(source)
}
