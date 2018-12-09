package com.example.rentals.controller

import com.example.rentals.service.AssetService
import com.example.rentals.domain.Asset
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID

internal class AssetControllerTest {
    val assetService = Mockito.mock(AssetService::class.java)

    @Test
    fun `should add the asset passed in the request to database`() {
        val assetController = AssetController(assetService)

        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")
        whenever(assetService.create(asset)).thenReturn(Mono.just(true))

        assetController.create(asset)

        verify(assetService, times(1)).create(asset)
    }

    @Test
    fun `should return the status 201 when the save is successful`() {
        val assetController = AssetController(assetService)

        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")

        whenever(assetService.create(asset)).thenReturn(Mono.just(true))

        assertEquals(ResponseEntity(asset, HttpStatus.CREATED), assetController.create(asset).block())
    }

    @Test
    fun `should return the status 422 when the save is unsuccessful`() {
        val assetController = AssetController(assetService)

        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")

        whenever(assetService.create(asset)).thenReturn(Mono.just(false))

        assertEquals(ResponseEntity<Asset>(HttpStatus.UNPROCESSABLE_ENTITY), assetController.create(asset).block())
    }

    @Test
    fun `should return the status 404 when the asset is not found in the database`() {
        val assetController = AssetController(assetService)
        val id = "65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"

        whenever(assetService.get(id)).thenReturn(Mono.empty())

        assertEquals(ResponseEntity<Asset>(HttpStatus.NOT_FOUND), assetController.get(id).block())
    }

    @Test
    fun `should return status 200 when the asset is found`() {
        val assetController = AssetController(assetService)
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")
        val id = asset.id.toString()

        whenever(assetService.get(id)).thenReturn(Mono.just(asset))

        val response = assetController.get(id).block()

        assertEquals(asset, response?.body)
        assertEquals(HttpStatus.OK, response?.statusCode)
    }

    @Test
    fun `should return the status 204 when the patch is successful`() {
        val assetController = AssetController(assetService)
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")
        val newAsset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Changed Category")
        val id = asset.id.toString()

        whenever(assetService.patch(id, newAsset)).thenReturn(true.toMono())

        val response = assetController.patch(id, newAsset).block()

        assertEquals(HttpStatus.NO_CONTENT, response?.statusCode)
    }

    @Test
    fun `should return the status 404 when the asset to patch is unsuccessful`() {
        val assetController = AssetController(assetService)
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")
        val newAsset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Changed Category")
        val id = asset.id.toString()

        whenever(assetService.patch(id, newAsset)).thenReturn(false.toMono())

        val response = assetController.patch(id, newAsset).block()

        assertEquals(HttpStatus.NOT_FOUND, response?.statusCode)
    }

    @Test
    fun `should return the status 204 when the delete is successful`() {
        val assetController = AssetController(assetService)
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")
        val id = asset.id.toString()

        whenever(assetService.delete(id)).thenReturn(true.toMono())

        val response = assetController.delete(id).block()

        assertEquals(HttpStatus.NO_CONTENT, response?.statusCode)
    }

    @Test
    fun `should return the status 404 when the delete is unsuccessful`() {
        val assetController = AssetController(assetService)
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")
        val id = asset.id.toString()

        whenever(assetService.delete(id)).thenReturn(false.toMono())

        val response = assetController.delete(id).block()

        assertEquals(HttpStatus.NOT_FOUND, response?.statusCode)
    }
}