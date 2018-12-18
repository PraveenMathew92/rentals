package com.example.rentals.service

import com.example.rentals.domain.Asset
import com.example.rentals.repository.AssetRepository
import com.example.rentals.util.UUIDorNil
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Service
class AssetService(private val assetRepository: AssetRepository) {
    fun create(asset: Asset): Mono<Boolean> {
        return with(assetRepository) {
            existsById(asset.id).map { it ->
                when (it) {
                    false -> save(asset).map { it == asset }
                    else -> false.toMono()
                }
            }
            .flatMap { it }
        }
    }

    fun get(id: String): Mono<Asset> {
        return assetRepository.findById(UUIDorNil(id))
    }

    fun patch(id: String, patch: String): Mono<Boolean> {
        val mapper = ObjectMapper()
        return get(id).flatMap { it ->
            mapper.readValue(
            JsonPatch.apply(stringToJsonNode(patch),
                    stringToJsonNode(mapper.writeValueAsString(it)))
                    .toString(),
                    Asset::class.java
                    ).toMono()
        }.flatMap { assetRepository.save(it).flatMap { true.toMono() }.defaultIfEmpty(false)
        }.switchIfEmpty(false.toMono())
    }

    fun delete(id: String): Mono<Boolean> {
        return with(assetRepository) {
            findById(UUIDorNil(id))
                    .flatMap { deleteById(UUIDorNil(id))
                            .then(true.toMono())
                    }
        }
                .defaultIfEmpty(false)
    }

    private fun stringToJsonNode(string: String): JsonNode = ObjectMapper().readTree(string)
}