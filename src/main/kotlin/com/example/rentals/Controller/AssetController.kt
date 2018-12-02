package com.example.rentals.Controller

import com.example.rentals.domain.Asset
import com.example.rentals.repository.AssetRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/asset")
class AssetController(val repository: AssetRepository){
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody asset: Asset) {
        repository.save(asset)
    }
}