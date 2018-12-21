package com.example.rentals.integrationTests

import com.example.rentals.domain.Customer
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@SpringBootTest
class CustomerIntegrationTest {
    @Autowired
    lateinit var context: ApplicationContext

    lateinit var client: WebTestClient

    @Test
    fun `should throw 400 if the contact number is less than 10`() {
        client = WebTestClient
                .bindToApplicationContext(context)
                .configureClient()
                .build()

        val customer = Customer("email@test.com", "Test", "123")
        client.post()
                .uri("/customer")
                .body(Mono.just(customer), Customer::class.java)
                .exchange()
                .expectStatus()
                .isBadRequest
    }

    @Test
    fun `should throw 400 if the email is invalid`() {
        client = WebTestClient
                .bindToApplicationContext(context)
                .configureClient()
                .build()

        val customer = Customer("emailtest.com", "Test", "1234567890")
        client.post()
                .uri("/customer")
                .body(Mono.just(customer), Customer::class.java)
                .exchange()
                .expectStatus()
                .isBadRequest
    }
}