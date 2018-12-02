package com.example.rentals.Controller

import com.example.rentals.domain.Asset
import com.example.rentals.repository.AssetRepository
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.util.*

internal class AssetControllerTest{
    val assetRepository = Mockito.mock(AssetRepository::class.java)

    private val assetController = AssetController(assetRepository)

    @Test
    fun `should add the asset passed in the request to database`(){
        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Some Asset",
                "Category")
        assetController.create(asset)

        verify(assetRepository, times(1)).save(asset)
    }
}