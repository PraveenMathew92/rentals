package com.example.rentals.controller

import com.example.rentals.domain.Asset
import com.example.rentals.service.AssetService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/asset")
class AssetController(private val assetService: AssetService) {
    @PostMapping
    fun create(@RequestBody asset: Asset): Mono<ResponseEntity<Asset>> {
        return assetService
                .create(asset)
                .map { it -> if (it) ResponseEntity(asset, HttpStatus.CREATED)
                    else ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY) }
    }
}