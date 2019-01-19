package com.example.rentals.service

import com.example.rentals.domain.asset.Asset
import com.example.rentals.domain.asset.AssetPrimaryKey
import com.example.rentals.domain.asset.AssetTable
import com.example.rentals.domain.asset.CategoryFields
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
    private val tenantId = 22

    private val assetTable = AssetTable(AssetPrimaryKey(tenantId, asset.id), asset)

    @Test
    fun `should call the save method of repository to create a new asset`() {
        val assetService = AssetService(assetRepository)

        val captor = argumentCaptor<AssetTable> {
            whenever(assetRepository.save(capture())).thenReturn(assetTable.toMono())
        }
        whenever(assetRepository.existsById(AssetPrimaryKey(tenantId, asset.id))).thenReturn(false.toMono())

        assetService.create(asset, tenantId).block()
        assertEquals(asset, captor.firstValue)
    }

    @Test
    fun `should return true when the asset is saved`() {
        whenever(assetRepository.save(assetTable)).thenReturn(assetTable.toMono())
        whenever(assetRepository.existsById(assetTable.primaryKey)).thenReturn(false.toMono())

        val assetService = AssetService(assetRepository)

        assertTrue(assetService.create(asset, tenantId).block()!!)
    }

    @Test
    fun `should return false when the asset save is not saved`() {
        val anotherAsset = Asset(UUID.fromString("752f3c7c-f449-4ea4-85e1-ad61dd2dbf53"),
                "Scorpio",
                CategoryFields("Maruti Suzuki", "Lxi", "7 Seater")
        )
        val anotherAssetTable = AssetTable(AssetPrimaryKey(tenantId, anotherAsset.id), anotherAsset)
        whenever(assetRepository.save(assetTable)).thenReturn(anotherAssetTable.toMono())
        whenever(assetRepository.existsById(assetTable.primaryKey)).thenReturn(false.toMono())

        val assetService = AssetService(assetRepository)

        assertFalse(assetService.create(asset, tenantId).block()!!)
    }

    @Test
    fun `should return an empty Mono if the asset id is not a UUID`() {
        val assetService = AssetService(assetRepository)

        whenever(assetRepository.findById(AssetPrimaryKey(tenantId, UUID(0,0))))
                .thenReturn(Mono.empty())

        assertNull(assetService.get("Non-UUID String", tenantId).block())
    }

    @Test
    fun `should return the asset if found in database`() {

        whenever(assetRepository.findById(AssetPrimaryKey(tenantId, asset.id))).thenReturn(assetTable.toMono())

        val assetService = AssetService(assetRepository)

        assertEquals(asset, assetService.get(asset.id.toString(), tenantId).block())
    }

    @Test
    fun `should return true if the asset patch is successful`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"name\", \"value\": \"Swift\"}]"
        val newAsset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Swift", CategoryFields("Maruti Suzuki", "Vxi", "5 Seater"))
        val assetService = AssetService(assetRepository)
        val newAssetTable = AssetTable(AssetPrimaryKey(tenantId, newAsset.id), newAsset)

        whenever(assetRepository.findById(AssetPrimaryKey(tenantId, asset.id))).thenReturn(assetTable.toMono())
        whenever(assetRepository.save(newAssetTable)).thenReturn(newAssetTable.toMono())

        assertTrue(assetService.patch(asset.id.toString(), patch, tenantId).block()!!)
    }

    @Test
    fun `should return false if the asset patch fails to update the asset`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"name\", \"value\": \"Swift\"}]"
        val newAsset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Swift",
                CategoryFields("Maruti Suzuki", "Vxi", "5 Seater")
        )
        val newAssetTable = AssetTable(AssetPrimaryKey(tenantId, newAsset.id), newAsset)
        val assetService = AssetService(assetRepository)

        whenever(assetRepository.findById(AssetPrimaryKey(tenantId, asset.id))).thenReturn(assetTable.toMono())
        whenever(assetRepository.save(newAssetTable)).thenReturn(Mono.empty())

        assertFalse(assetService.patch(asset.id.toString(), patch, tenantId).block()!!)
    }

    @Test
    fun `should return false if the asset patch fails to fetch the asset`() {
        val patch = "[{\"op\": \"replace\", \"path\":\"category/type\", \"value\": \"Lxi\"}]"
        val assetService = AssetService(assetRepository)

        whenever(assetRepository.findById(AssetPrimaryKey(tenantId, asset.id))).thenReturn(Mono.empty())

        assertFalse(assetService.patch(asset.id.toString(), patch, tenantId).block()!!)
    }

    @Test
    fun `should return true if the user has been deleted successfully`() {
        whenever(assetRepository.findById(AssetPrimaryKey(tenantId, asset.id))).thenReturn(assetTable.toMono())
        whenever(assetRepository.deleteById(any<AssetPrimaryKey>()))
                .thenReturn(Mono.create { it.success(null) })

        val assetService = AssetService(assetRepository)

        assertTrue(assetService.delete(asset.id.toString(), tenantId).block()!!)
    }

    @Test
    fun `should return false if the asset to be deleted is not found in the database`() {
        whenever(assetRepository.findById(AssetPrimaryKey(tenantId, asset.id))).thenReturn(Mono.empty())
        val assetService = AssetService(assetRepository)

        assertFalse(assetService.delete(asset.id.toString(), tenantId).block()!!)
    }

    @Test
    fun `should not create an asset and return false if the key exists`() {
        whenever(assetRepository.existsById(AssetPrimaryKey(tenantId, asset.id))).thenReturn(true.toMono())
        val assetService = AssetService(assetRepository)

        assertFalse(assetService.create(asset, tenantId).block()!!)
        verify(assetRepository, never()).save(assetTable)
    }

    @Test
    fun `should return true if the asset is present in the database`() {
        whenever(assetRepository.existsById(AssetPrimaryKey(tenantId, asset.id))).thenReturn(true.toMono())
        val assetService = AssetService(assetRepository)

        assetService.exists(asset.id, tenantId).subscribe { assertTrue(it) }
    }

    @Test
    fun `should return false if the asset is not present in the database`() {
        whenever(assetRepository.existsById(AssetPrimaryKey(tenantId, asset.id))).thenReturn(false.toMono())
        val assetService = AssetService(assetRepository)

        assetService.exists(asset.id, tenantId).subscribe { assertFalse(it) }
    }
}