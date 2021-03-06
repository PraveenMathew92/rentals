package com.example.rentals.controller

import com.example.rentals.domain.Asset
import com.example.rentals.domain.Customer
import com.example.rentals.domain.Order
import com.example.rentals.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.DeleteMapping
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID

@RestController
class OrderController(val orderService: OrderService) {
    @PostMapping
    fun create(@RequestBody order: Order): Mono<ResponseEntity<Order>> {
        return orderService.create(order).map { when (it) {
            true -> ResponseEntity<Order>(HttpStatus.CREATED)
            else -> ResponseEntity<Order>(HttpStatus.CONFLICT)
        } }
    }

    @GetMapping("/order/customer/{email}/asset/{assetId}")
    fun get(@PathVariable("email") email: String, @PathVariable("assetId") assetId: String): Mono<ResponseEntity<Order>> {
        return orderService.get(email, assetId)
                .map { ResponseEntity(it, HttpStatus.OK) }
                .defaultIfEmpty(ResponseEntity<Order>(HttpStatus.NOT_FOUND))
    }

    @DeleteMapping("/order/customer/{email}/asset/{assetId}")
    fun delete(@PathVariable("email") email: String, @PathVariable("assetId") assetId: String): Mono<ResponseEntity<Order>> {
        return orderService.delete(email, assetId).map { when (it) {
                true -> ResponseEntity<Order>(HttpStatus.NO_CONTENT)
                else -> ResponseEntity<Order>(HttpStatus.NOT_FOUND)
            }
        }
    }

    @PatchMapping("/order/customer/{email}/asset/{assetId}")
    fun patch(@PathVariable("email") email: String, @PathVariable("assetId") assetId: String, @RequestBody patch: String): Mono<ResponseEntity<Order>> {
        return orderService.patch(email, assetId, patch).map { when (it) {
            true -> ResponseEntity<Order>(HttpStatus.NO_CONTENT)
            else -> ResponseEntity<Order>(HttpStatus.NOT_FOUND)
        } }
    }

    @DeleteMapping("customer/{email}")
    fun safeDeleteCustomer(@PathVariable email: String): Mono<ResponseEntity<Customer>> {
        return orderService.safeDeleteCustomer(email)
                .flatMap { when (it) {
                    true -> ResponseEntity<Customer>(HttpStatus.NO_CONTENT).toMono()
                    else -> ResponseEntity<Customer>(HttpStatus.NOT_FOUND).toMono()
                } }
    }

    @DeleteMapping("asset/{id}")
    fun safeDeleteAsset(@PathVariable id: UUID): Mono<ResponseEntity<Asset>> {
        return orderService.safeDeleteAsset(id).map {
            when (it) {
                true -> ResponseEntity<Asset>(HttpStatus.NO_CONTENT)
                else -> ResponseEntity<Asset>(HttpStatus.NOT_FOUND)
            }
        }
    }
}