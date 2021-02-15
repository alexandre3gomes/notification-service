package com.finances.chatservice.integration.service

import com.finances.chatservice.config.MongoSpringBootTest
import com.finances.chatservice.model.UserChannel
import com.finances.chatservice.repository.ChannelRepository
import com.finances.chatservice.repository.UserChannelRepository
import com.finances.chatservice.service.ChannelService
import com.finances.chatservice.service.MessageService
import com.finances.chatservice.util.BaseMockDataTest.ADMIN_EMAIL
import com.finances.chatservice.util.BaseMockDataTest.CHANNEL_NAME
import com.finances.chatservice.util.BaseMockDataTest.getChannel
import com.finances.chatservice.util.BaseMockDataTest.getDefaultChannel
import com.finances.chatservice.util.BaseMockDataTest.getMessage
import com.finances.chatservice.util.BaseMockDataTest.getPrincipal
import com.finances.chatservice.util.BaseMockDataTest.getUserChannel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import reactor.test.StepVerifier

@MongoSpringBootTest
class ChannelServiceTest(
    @Autowired val channelService: ChannelService,
    @Autowired val channelRepository: ChannelRepository,
    @Autowired val messageService: MessageService,
    @Autowired val userChannelRepository: UserChannelRepository
) {

    @BeforeEach
    fun cleanUp() {
        channelService.deleteChannel(CHANNEL_NAME)
        userChannelRepository.deleteAll().subscribe()
    }

    @Test
    fun `Get first message by channel`() {
        channelService.createChannel(getChannel(), null)
        messageService.saveMessage(getMessage())
        val result = channelService.getMessagesByChannel(getMessage().channel)
        StepVerifier
            .create(result.take(1))
            .expectNext(getMessage())
            .expectComplete()
            .verify()
    }

    @Nested
    inner class TestListChannels {

        @Test
        fun `List channels of admin user`() {
            channelService.createChannel(getChannel(), JwtAuthenticationToken(getPrincipal()))
            StepVerifier.create(channelService.listChannel(JwtAuthenticationToken(getPrincipal())))
                .expectNext(getUserChannel())
                .expectComplete()
                .verify()
        }

        @Test
        fun `Empty list channels of admin user`() {
            StepVerifier.create(channelService.listChannel(JwtAuthenticationToken(getPrincipal())))
                .expectComplete()
                .verify()
        }
    }

    @Nested
    inner class TestCreateChannel {

        @Test
        fun `Create channel with Principal`() {
            channelService.createChannel(getChannel(), JwtAuthenticationToken(getPrincipal()))
            val channels = channelRepository.findAll()
            val userChannels = userChannelRepository.findAll().map(UserChannel::user)
            StepVerifier.create(channels)
                .expectNext(getDefaultChannel())
                .expectNext(getChannel())
                .expectComplete()
                .verify()
            Thread.sleep(100)
            StepVerifier.create(userChannels)
                .expectNext(ADMIN_EMAIL)
                .expectComplete()
                .verify()
        }

        @Test
        fun `Create channel without Principal`() {
            channelService.createChannel(getChannel())
            val channels = channelRepository.findAll()
            val userChannels = userChannelRepository.findAll()
            StepVerifier.create(channels)
                .expectNext(getDefaultChannel())
                .expectNext(getChannel())
                .expectComplete()
                .verify()
            Thread.sleep(100)
            StepVerifier.create(userChannels)
                .expectComplete()
                .verify()
        }
    }

}