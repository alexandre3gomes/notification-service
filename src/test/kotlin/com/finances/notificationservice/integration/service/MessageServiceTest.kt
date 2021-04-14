package com.finances.notificationservice.integration.service

import com.finances.notificationservice.repository.ChannelCustomRepository
import com.finances.notificationservice.service.MessageService
import com.finances.notificationservice.util.BaseMockDataTest.CHANNEL_NAME
import com.finances.notificationservice.util.BaseMockDataTest.getMessage
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureDataMongo
class MessageServiceTest(
    @Autowired val service: MessageService,
    @Autowired val repository: ChannelCustomRepository
) {

    @BeforeEach
    fun cleanUp() {
        repository.deleteCollection(CHANNEL_NAME)
    }

    @Test
    fun `Save message and expect disposable to be not null`() {
        val disposable = service.saveMessage(getMessage())
        disposable shouldNotBe null
    }

}