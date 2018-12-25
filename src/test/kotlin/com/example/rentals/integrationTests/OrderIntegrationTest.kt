package com.example.rentals.integrationTests

import com.example.rentals.domain.Customer
import com.example.rentals.domain.Asset
import com.example.rentals.domain.CategoryFields
import com.example.rentals.domain.OrderPrimaryKey
import com.example.rentals.domain.Order
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID
import java.util.Date

@RunWith(SpringRunner::class)
@SpringBootTest
class OrderIntegrationTest {
    @Autowired
    lateinit var context: ApplicationContext

    lateinit var client: WebTestClient

    @Before
    fun setup() {
        client = WebTestClient
                .bindToApplicationContext(context)
                .configureClient()
                .build()
    }

    @Test
    fun `basic CRUD`() {
        val customer = Customer("email@test.com", "Test", 1234567890)

        val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
                "Swift Dzire",
                CategoryFields("Maruti Suzuki", "Vxi", "5 Seater")
        )

        val order = Order(OrderPrimaryKey(customer.email, asset.id), Date(), 1000)

        client.post()
                .uri("/order")
                .body(Mono.just(order), Order::class.java)
                .exchange()
                .expectStatus()
                .isCreated

        client.get()
                .uri("/order/customer/email@test.com/asset/65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
                .exchange()
                .expectStatus().isOk
                .expectBody(Customer::class.java)

        client.patch()
                .uri("/order/customer/email@test.com/asset/65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
                .body("[{\"op\": \"replace\", \"path\":\"rate\", \"value\": \"1000\"}]".toMono(),
                        String::class.java)
                .exchange()
                .expectStatus().isNoContent
                .expectBody(Customer::class.java)

        client.delete()
                .uri("/order/customer/email@test.com/asset/65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
                .exchange()
                .expectStatus()
                .isNoContent
    }
}