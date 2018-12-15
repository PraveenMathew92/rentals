package com.example.rentals.service

import com.example.rentals.domain.Asset
import com.example.rentals.domain.CategoryFields
import com.example.rentals.repository.AssetRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID

class AssetServiceTest {
    private val assetRepository: AssetRepository = mock()

    private val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Swift Dzire",
            CategoryFields("Maruti Suzuki", "Vxi", "5 Seater")
    )

    @Test
    fun `should call the save method of repository to create a new asset`() {
        val assetService = AssetService(assetRepository)

        val captor = argumentCaptor<Asset> {
            whenever(assetRepository.save(capture())).thenReturn(Mono.just(asset))
        }
        whenever(assetRepository.existsById(asset.id)).thenReturn(false.toMono())

        assetService.create(asset).block()
        assertEquals(asset, captor.firstValue)
    }

    @Test
    fun `should return true when the asset is saved`() {
        whenever(assetRepository.save(asset)).thenReturn(Mono.just(asset))
        whenever(assetRepository.existsById(asset.id)).thenReturn(false.toMono())

        val assetService = AssetService(assetRepository)

        assertTrue(assetService.create(asset).block()!!)
    }

    @Test
    fun `should return false when the asset save is not saved`() {
        val anotherAsset = Asset(UUID.fromString("752f3c7c-f449-4ea4-85e1-ad61dd2dbf53"),
                "Scorpio",
                CategoryFields("Maruti Suzuki", "Lxi", "7 Seater")
        )
                        whenever(assetRepository.save(asset)).thenReturn(Mono.just(anotherAsset))
        whenever(assetRepository.existsById(asset.id)).thenReturn(false.toMono())

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

        whenever(assetRepository.findById(asset.id)).thenReturn(asset.toMono())

        val assetService = AssetService(assetRepository)

        assertEquals(asset, assetService.get(asset.id.toString()).block())
    }

    @Test
    fun `should return true if the asset patch is successful`() {
        val newAsset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Swift Dzire",
                CategoryFields("Maruti Suzuki", "Lxi", "5 Seater")

        )
                val assetService = AssetService(assetRepository)

        whenever(assetRepository.findById(asset.id)).thenReturn(asset.toMono())
        whenever(assetRepository.save(newAsset)).thenReturn(newAsset.toMono())

        assertTrue(assetService.patch(asset.id.toString(), newAsset).block()!!)
    }

    @Test
    fun `should return false if the asset patch fails to update the asset`() {
        val newAsset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Swift Dzire",
                CategoryFields("Maruti Suzuki", "Lxi", "5 Seater")
        )
                val assetService = AssetService(assetRepository)

        whenever(assetRepository.findById(asset.id)).thenReturn(asset.toMono())
        whenever(assetRepository.save(newAsset)).thenReturn(asset.toMono())

        assertFalse(assetService.patch(asset.id.toString(), newAsset).block()!!)
    }

    @Test
    fun `should return false if the asset patch fails to fetch the asset`() {
        val newAsset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Swift Dzire",
                CategoryFields("Maruti Suzuki", "Lxi", "5 Seater")
        )
                val assetService = AssetService(assetRepository)

        whenever(assetRepository.findById(asset.id)).thenReturn(Mono.empty())

        assertFalse(assetService.patch(asset.id.toString(), newAsset).block()!!)
    }

    @Test
    fun `should return true if the user has been deleted successfully`() {
        whenever(assetRepository.findById(asset.id)).thenReturn(asset.toMono())
        whenever(assetRepository.deleteById(any<UUID>()))
                .thenReturn(Mono.create { it.success(null) })

        val assetService = AssetService(assetRepository)

        assertTrue(assetService.delete(asset.id.toString()).block()!!)
    }

    @Test
    fun `should return false if the asset to be deleted is not found in the database`() {
        whenever(assetRepository.findById(asset.id)).thenReturn(Mono.empty())
        val assetService = AssetService(assetRepository)

        assertFalse(assetService.delete(asset.id.toString()).block()!!)
    }

    @Test
    fun `should not create an asset and return false if the key exists`() {
        whenever(assetRepository.existsById(asset.id)).thenReturn(true.toMono())
        val assetService = AssetService(assetRepository)

        assertFalse(assetService.create(asset).block()!!)
        verify(assetRepository, never()).save(asset)
    }
}