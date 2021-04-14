package com.finances.notificationservice.unit.service

import com.finances.notificationservice.repository.MessageRepository
import com.finances.notificationservice.service.MessageService
import com.finances.notificationservice.util.BaseMockDataTest.getMessage
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Mono

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig(MessageService::class)
class MessageServiceTest(
    @Autowired private val service: MessageService
) {

    @MockkBean
    lateinit var repository: MessageRepository

    @Test
    fun `Create message with content`() {
        every { repository.persistMessage(any()) } returns Mono.just(getMessage())
        val result = service.saveMessage(getMessage())
        result.isDisposed shouldNotBe null
        result.isDisposed shouldBe true
        verify(exactly = 1) { repository.persistMessage(any()) }
    }
}