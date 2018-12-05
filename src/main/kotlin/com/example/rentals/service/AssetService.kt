package com.example.rentals.service

import com.example.rentals.domain.Asset
import com.example.rentals.repository.AssetRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AssetService(private val assetRepository: AssetRepository) {
    fun create(asset: Asset): Mono<Boolean> {
        return assetRepository
                .save(asset)
                .map { it -> it == asset }
    }
}