package com.finances.notificationservice.integration.controller

import com.finances.notificationservice.controller.MessageController
import com.finances.notificationservice.model.Message
import com.finances.notificationservice.service.MessageService
import com.finances.notificationservice.util.BaseMockDataTest.getMessage
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitWebConfig
@AutoConfigureDataMongo
@AutoConfigureWebTestClient
@WebFluxTest(MessageController::class)
class MessageControllerTest{

    @MockkBean
    lateinit var service: MessageService

    @Autowired
    lateinit var client: WebTestClient

    @Test
    @WithMockUser
    fun `Send a message to test channel and expect message to be saved`() {
        every { service.saveMessage(any()) } returns Mono.just(getMessage()).subscribe()
        client
            .mutateWith(csrf())
            .post()
            .uri(BASE_URL)
            .body(Mono.just(getMessage()), Message::class.java)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.disposed").isEqualTo(true)
            .jsonPath("$.scanAvailable").isEqualTo(true)
        verify(exactly = 1) { service.saveMessage(any()) }
    }

    companion object {
        private const val BASE_URL = "/message/"
    }
}