package com.example.rentals.controller

import com.example.rentals.domain.asset.Asset
import com.example.rentals.service.AssetService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@RestController
@RequestMapping("/asset")
class AssetController(private val assetService: AssetService) {
    @PostMapping
    fun create(@RequestHeader tenantId: Int, @RequestBody asset: Asset): Mono<ResponseEntity<Asset>> {
        return assetService
                .create(asset, tenantId)
                .map { it -> if (it) ResponseEntity(asset, HttpStatus.CREATED)
                    else ResponseEntity(HttpStatus.CONFLICT) }
    }

    @GetMapping("/{id}")
    fun get(@RequestHeader tenantId: Int, @PathVariable id: String): Mono<ResponseEntity<Asset>> {
        return assetService
                .get(id, tenantId)
                .map { ResponseEntity<Asset>(it, HttpStatus.OK) }
                .switchIfEmpty(ResponseEntity<Asset>(HttpStatus.NOT_FOUND).toMono())
    }

    @PatchMapping("/{id}")
    fun patch(@RequestHeader tenantId: Int, @PathVariable id: String, @RequestBody patch: String): Mono<ResponseEntity<Asset>> {
        return assetService.patch(id, patch, tenantId).map {
            when (it) {
                true -> ResponseEntity<Asset>(HttpStatus.NO_CONTENT)
                else -> ResponseEntity<Asset>(HttpStatus.NOT_FOUND)
            }
        }
    }
}