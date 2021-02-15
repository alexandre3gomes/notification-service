package com.finances.chatservice.integration.service

import com.finances.chatservice.repository.ChannelCustomRepository
import com.finances.chatservice.service.MessageService
import com.finances.chatservice.util.BaseMockDataTest.CHANNEL_NAME
import com.finances.chatservice.util.BaseMockDataTest.getMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest

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
        assertThat(disposable).isNotNull
    }

}