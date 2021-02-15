package com.finances.chatservice.integration.controller

import com.finances.chatservice.controller.AuthController
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig
import org.springframework.test.web.reactive.server.WebTestClient

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitWebConfig
@AutoConfigureDataMongo
@WebFluxTest(AuthController::class)
class AuthControllerTest (@Autowired private val client: WebTestClient){

    @Test
    @WithMockUser
    fun `Test authentication with mocked UserDetails and expect HTTP 500 error`() {
        client.mutateWith(mockJwt())
            .get()
            .uri(TEST_URL)
            .exchange()
            .expectStatus().is5xxServerError
    }

    companion object {
        const val TEST_URL = "/auth/token"
    }
}