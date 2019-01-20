package com.example.rentals.converter

import com.example.rentals.domain.asset.Asset
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import java.io.IOException
import java.lang.IllegalStateException

class JSONToAssetConverter : Converter<String, Asset> {
    override fun convert(source: String): Asset {
        try {
            return ObjectMapper().readValue(source, Asset::class.java)
        } catch (exception: IOException) {
            throw IllegalStateException(exception)
        }
    }
}

class AssetToJSONConverter : Converter<Asset, String> {
    override fun convert(source: Asset): String =
            ObjectMapper().writeValueAsString(source)
}