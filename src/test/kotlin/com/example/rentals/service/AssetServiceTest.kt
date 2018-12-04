package com.example.rentals.service

import com.example.rentals.domain.Asset
import com.example.rentals.repository.AssetRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import java.util.*

class AssetServiceTest{
    private val assetRepository : AssetRepository = mock()
    val assetService = AssetService(assetRepository)

    @Test
    fun `should call the save method of repository to create a new asset`() {
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")

        assetService.create(asset)

        verify(assetRepository).save(asset)
    }
}