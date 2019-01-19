package com.example.rentals.service

import com.example.rentals.domain.asset.Asset
import com.example.rentals.domain.asset.AssetPrimaryKey
import com.example.rentals.domain.asset.AssetTable
import com.example.rentals.repository.AssetRepository
import com.example.rentals.util.UUIDorNil
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID

@Service
class AssetService(private val assetRepository: AssetRepository) {
    fun create(asset: Asset, tenantId: Int): Mono<Boolean> {
        val primaryKey = AssetPrimaryKey(tenantId, asset.id)
        return with(assetRepository) {
            existsById(primaryKey).map { it ->
                when (it) {
                    false -> save(AssetTable(primaryKey, asset)).map { it.asset == asset }
                    else -> false.toMono()
                }
            }
            .flatMap { it }
        }
    }

    fun get(id: String, tenantId: Int): Mono<Asset> {
        return assetRepository.findById(AssetPrimaryKey(tenantId, UUIDorNil(id))).map { it.asset }
    }

    fun patch(id: String, patch: String, tenantId: Int): Mono<Boolean> {
        val primaryKey = AssetPrimaryKey(tenantId, UUIDorNil(id))
        val mapper = ObjectMapper()
        return get(id, tenantId).flatMap { it ->
            mapper.readValue(
            JsonPatch.apply(stringToJsonNode(patch),
                    stringToJsonNode(mapper.writeValueAsString(it)))
                    .toString(),
                    Asset::class.java
                    ).toMono()
        }.flatMap { assetRepository.save(AssetTable(primaryKey, it))
                .flatMap { true.toMono() }
                .defaultIfEmpty(false)
        }.switchIfEmpty(false.toMono())
    }

    fun delete(id: String, tenantId: Int): Mono<Boolean> {
        val primaryKey = AssetPrimaryKey(tenantId, UUIDorNil(id))
        return (assetRepository.findById(primaryKey)
                .flatMap { assetRepository.deleteById(primaryKey)
                        .then(true.toMono())
                })
                .defaultIfEmpty(false)
    }

    fun exists(assetId: UUID, tenantId: Int): Mono<Boolean> {
        val primaryKey = AssetPrimaryKey(tenantId, assetId)
        return assetRepository.existsById(primaryKey)
    }

    private fun stringToJsonNode(string: String): JsonNode = ObjectMapper().readTree(string)
}