package com.example.rentals.controller

import com.example.rentals.domain.Order
import com.example.rentals.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.DeleteMapping
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/order")
class OrderController(val orderService: OrderService) {
    @PostMapping
    fun create(order: Order): Mono<ResponseEntity<Order>> {
        return orderService.create(order).map { when (it) {
            true -> ResponseEntity<Order>(HttpStatus.CREATED)
            else -> ResponseEntity<Order>(HttpStatus.CONFLICT)
        } }
    }

    @GetMapping("/customer/{email}/asset/{assetId}")
    fun get(@PathVariable email: String, @PathVariable assetId: String): Mono<ResponseEntity<Order>> {
        return orderService.get(email, assetId)
                .map { ResponseEntity(it, HttpStatus.OK) }
                .defaultIfEmpty(ResponseEntity<Order>(HttpStatus.NOT_FOUND))
    }

    @DeleteMapping("/customer/{email}/asset/{assetId}")
    fun delete(@PathVariable email: String, @PathVariable assetId: String): Mono<ResponseEntity<Order>> {
        return orderService.delete(email, assetId).map { when (it) {
                true -> ResponseEntity<Order>(HttpStatus.NO_CONTENT)
                else -> ResponseEntity<Order>(HttpStatus.NOT_FOUND)
            }
        }
    }

    @PatchMapping("/customer/{email}/asset/{assetId}")
    fun patch(@PathVariable email: String, @PathVariable assetId: String, @RequestBody patch: String): Mono<ResponseEntity<Order>> {
        return orderService.patch(email, assetId, patch).map { when (it) {
            true -> ResponseEntity<Order>(HttpStatus.NO_CONTENT)
            else -> ResponseEntity<Order>(HttpStatus.NOT_FOUND)
        } }
    }
}