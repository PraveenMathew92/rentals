@file:Suppress("DEPRECATION")

package com.example.rentals.service

import com.example.rentals.domain.Asset
import com.example.rentals.repository.AssetRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID

class AssetServiceTest {
    private val assetRepository: AssetRepository = mock()

    @Test
    fun `should call the save method of repository to create a new asset`() {
        val assetService = AssetService(assetRepository)

        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")
        whenever(assetRepository.save(asset)).thenReturn(Mono.just(asset))

        assetService.create(asset)

        verify(assetRepository).save(asset)
    }

    @Test
    fun `should return true when the asset is saved`() {
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")
        whenever(assetRepository.save(asset)).thenReturn(Mono.just(asset))

        val assetService = AssetService(assetRepository)

        assertTrue(assetService.create(asset).block()!!)
    }

    @Test
    fun `should return false when the asset save is not saved`() {
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")
        val anotherAsset = Asset(UUID.fromString("752f3c7c-f449-4ea4-85e1-ad61dd2dbf53"),
                "Some Asset",
                "Category")
        whenever(assetRepository.save(asset)).thenReturn(Mono.just(anotherAsset))

        val assetService = AssetService(assetRepository)

        assertFalse(assetService.create(asset).block()!!)
    }

    @Test
    fun `should return an empty Mono if the asset id is not a UUID`() {
        val assetService = AssetService(assetRepository)

        whenever(assetRepository.findById(UUID(0, 0))).thenReturn(Mono.empty())

        assertNull(assetService.get("Non-UUID String").block())
    }

    @Test
    fun `should return the asset if found in database`() {
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")

        whenever(assetRepository.findById(asset.id)).thenReturn(asset.toMono())

        val assetService = AssetService(assetRepository)

        assertEquals(asset, assetService.get(asset.id.toString()).block())
    }
}