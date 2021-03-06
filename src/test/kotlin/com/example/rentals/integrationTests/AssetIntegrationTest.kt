package com.example.rentals.integrationTests

import com.example.rentals.domain.Asset
import com.example.rentals.domain.CategoryFields
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID

@RunWith(SpringRunner::class)
@SpringBootTest
class AssetIntegrationTest {
    val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
            "Swift Dzire",
            CategoryFields("Maruti Suzuki", "Vxi", "5 Seater")
    )

    val assetWithCorrectedType = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
    "Swift",
            CategoryFields("Maruti Suzuki", "Lxi", "5 Seater")
    )

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
    fun `test CRUD`() {
        client.post().uri("/asset")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(asset), Asset::class.java)
                .exchange()
                .expectStatus().isCreated

        client.get().uri("/asset/65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
                .exchange()
                .expectStatus().isOk
                .expectBody(Asset::class.java)

        client.patch().uri("/asset/65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
                .accept(MediaType.APPLICATION_JSON)
                .body("[{\"op\": \"replace\", \"path\":\"name\", \"value\": \"Swift\"}]".toMono(),
                        String::class.java)
                .exchange()
                .expectStatus().isNoContent
                .expectBody(Asset::class.java)

        client.delete().uri("/asset/65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
                .exchange()
                .expectStatus().isNoContent
    }

    @Test
    fun `should return 404 if the asset is not found`() {
        client.patch().uri("/asset/752f3c7c-f449-4ea4-85e1-ad61dd2dbf53")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(assetWithCorrectedType), Asset::class.java)
                .exchange()
                .expectStatus().isNotFound
    }
}