package com.example.rentals.converter

import com.example.rentals.domain.asset.AssetPrimaryKey
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import java.io.IOException
import java.lang.IllegalStateException

class JSONToAssetPrimaryKeyConverter : Converter<String, AssetPrimaryKey> {
    override fun convert(source: String): AssetPrimaryKey {
        try {
            return ObjectMapper().readValue(source, AssetPrimaryKey::class.java)
        } catch (exception: IOException) {
            throw IllegalStateException(exception)
        }
    }
}

class AssetPrimaryKeyToJSONConverter : Converter<AssetPrimaryKey, String> {
    override fun convert(source: AssetPrimaryKey): String =
            ObjectMapper().writeValueAsString(source)
}