package com.example.rentals.integrationTests

import com.example.rentals.domain.Asset
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
import java.util.UUID

@RunWith(SpringRunner::class)
@SpringBootTest
class AssetIntegrationTest {
    val asset = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
            "Swift Dzire",
            mapOf(Pair("Maker", "Maruti Suzuki"), Pair("Type", "Vxi"), Pair("Size", "5 Seater"), Pair("Quality", "7 km per liter")))
    val assetWithLesserQuality = Asset(UUID.fromString("65cf3c7c-f449-4cd4-85e1-bc61dd2db64e"),
            "Some Asset",
            mapOf(Pair("Maker", "Maruti Suzuki"), Pair("Type", "Vxi"), Pair("Size", "5 Seater"), Pair("Quality", "5 km per liter")))

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
    fun `should create an asset`() {
        client.post().uri("/asset")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(asset), Asset::class.java)
                .exchange()
                .expectStatus().isCreated
    }

    @Test
    fun `should get the created asset`() {
        client.get().uri("/asset/65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
                .exchange()
                .expectStatus().isOk
                .expectBody(Asset::class.java)
    }

    @Test
    fun `should patch the asset`() {
        client.patch().uri("/asset/65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(assetWithLesserQuality), Asset::class.java)
                .exchange()
                .expectStatus().isNoContent
                .expectBody(Asset::class.java)
    }

    @Test
    fun `should delete the asset`() {
        client.delete().uri("/asset/65cf3c7c-f449-4cd4-85e1-bc61dd2db64e")
                .exchange()
                .expectStatus().isNoContent
    }

    @Test
    fun `should return 404 if the asset is not found`() {
        client.patch().uri("/asset/752f3c7c-f449-4ea4-85e1-ad61dd2dbf53")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(assetWithLesserQuality), Asset::class.java)
                .exchange()
                .expectStatus().isNotFound
    }
}