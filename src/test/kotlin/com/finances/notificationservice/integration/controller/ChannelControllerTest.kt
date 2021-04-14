package com.finances.notificationservice.integration.controller

import com.finances.notificationservice.controller.ChannelController
import com.finances.notificationservice.model.Channel
import com.finances.notificationservice.model.Message
import com.finances.notificationservice.service.ChannelService
import com.finances.notificationservice.util.BaseMockDataTest.getChannel
import com.finances.notificationservice.util.BaseMockDataTest.getMessage
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitWebConfig
@AutoConfigureDataMongo
@AutoConfigureWebTestClient
@WebFluxTest(ChannelController::class)
class ChannelControllerTest {

    @MockkBean
    lateinit var service: ChannelService

    @Autowired
    lateinit var client: WebTestClient

    @Test
    @WithMockUser
    fun `Create channel endpoint and expect HTTP status 200`() {
        every { service.createChannel(any(), any()) } just Runs
        client
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri(BASE_URL)
            .body(Mono.just(getChannel(false)), Channel::class.java)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
        verify(exactly = 1) { service.createChannel(any(), any()) }
    }

    @Test
    @WithMockUser
    fun `Get messages by channel endpoint and expect HTTP status 200`() {
        every { service.getMessagesByChannel(any(), any()) } returns Flux.just(getMessage())
        val ret = client
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .get()
            .uri("$BASE_URL$CHANNEL_ID")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.parseMediaType("text/event-stream;charset=UTF-8"))
            .returnResult(Message::class.java).responseBody
        StepVerifier.create(ret)
            .expectNext(getMessage())
            .expectComplete()
            .verify()
        verify(exactly = 1) { service.getMessagesByChannel(any(), any()) }
    }

    companion object {
        private const val BASE_URL = "/channel/"
        private const val CHANNEL_ID = "id"
    }
}