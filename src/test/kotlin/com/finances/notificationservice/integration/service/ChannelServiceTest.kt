package com.finances.notificationservice.integration.service

import com.finances.notificationservice.config.MongoSpringBootTest
import com.finances.notificationservice.exception.ExceptionMessages.PRIVATE_CHANNEL_WITHOUT_PRINCIPAL
import com.finances.notificationservice.exception.ExceptionMessages.USER_HAS_NO_ACCESS
import com.finances.notificationservice.exception.NotAuthorizedException
import com.finances.notificationservice.model.Channel
import com.finances.notificationservice.model.UserChannel
import com.finances.notificationservice.repository.ChannelRepository
import com.finances.notificationservice.repository.UserChannelRepository
import com.finances.notificationservice.service.ChannelService
import com.finances.notificationservice.service.MessageService
import com.finances.notificationservice.util.BaseMockDataTest.ADMIN_EMAIL
import com.finances.notificationservice.util.BaseMockDataTest.CHANNEL_NAME
import com.finances.notificationservice.util.BaseMockDataTest.getChannel
import com.finances.notificationservice.util.BaseMockDataTest.getDefaultChannel
import com.finances.notificationservice.util.BaseMockDataTest.getMessage
import com.finances.notificationservice.util.BaseMockDataTest.getPrincipal
import com.finances.notificationservice.util.BaseMockDataTest.getUserChannel
import com.finances.notificationservice.util.BaseMockDataTest.getWrongPrincipal
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import reactor.test.StepVerifier

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
        userChannelRepository.deleteAll().block()
    }

    @Nested
    inner class TestGetMessagesByChannel {

        @Test
        fun `Get first message of a public channel`() {
            runBlocking {
                channelService.createChannel(getChannel(), null)
                delay(100)
                messageService.saveMessage(getMessage())
                val result = channelService.getMessagesByChannel(getMessage().channel, JwtAuthenticationToken(getPrincipal()))
                StepVerifier
                    .create(result.take(1))
                    .expectNext(getMessage())
                    .expectComplete()
                    .verify()
            }
        }

        @Test
        fun `Get first message of a private channel`() {
            runBlocking {
                channelService.createChannel(getChannel(true), JwtAuthenticationToken(getPrincipal()))
                delay(100)
                messageService.saveMessage(getMessage())
                val result = channelService.getMessagesByChannel(getMessage().channel, JwtAuthenticationToken(getPrincipal()))
                StepVerifier
                    .create(result.take(1))
                    .expectNext(getMessage())
                    .expectComplete()
                    .verify()
            }
        }

        @Test
        fun `Get empty response a private channel`() {
            runBlocking {
                channelService.createChannel(getChannel(true), JwtAuthenticationToken(getPrincipal()))
                messageService.saveMessage(getMessage())
                val result = channelService.getMessagesByChannel(getMessage().channel, JwtAuthenticationToken(getWrongPrincipal()))
                StepVerifier
                    .create(result)
                    .expectErrorMatches { it is NotAuthorizedException && it.message == USER_HAS_NO_ACCESS.format(CHANNEL_NAME) }
                    .verify()
            }
        }
    }

    @Nested
    inner class TestListChannels {

        @Test
        fun `List channels of admin user`() {
            runBlocking {
                channelService.createChannel(getChannel(), JwtAuthenticationToken(getPrincipal()))
                delay(100)
                StepVerifier.create(channelService.listChannels(JwtAuthenticationToken(getPrincipal())))
                    .expectNext(getUserChannel())
                    .expectComplete()
                    .verify()
            }
        }

        @Test
        fun `Empty list channels of admin user`() {
            StepVerifier.create(channelService.listChannels(JwtAuthenticationToken(getPrincipal())))
                .expectComplete()
                .verify()
        }
    }

    @Nested
    inner class TestCreateChannel {

        @Test
        fun `Create public channel with Principal`() {
            runBlocking {
                channelService.createChannel(getChannel(), JwtAuthenticationToken(getPrincipal()))
                delay(100)
                val channels = channelRepository.findAll()
                val userChannels = userChannelRepository.findAll().map(UserChannel::user)
                StepVerifier.create(channels)
                    .expectNext(getDefaultChannel())
                    .expectNext(getChannel())
                    .expectComplete()
                    .verify()
                StepVerifier.create(userChannels)
                    .expectNext(ADMIN_EMAIL)
                    .expectComplete()
                    .verify()
            }
        }

        @Test
        fun `Create public channel without Principal`() {
            runBlocking {
                channelService.createChannel(getChannel())
                delay(100)
                val channels = channelRepository.findAll()
                val userChannels = userChannelRepository.findAll()
                StepVerifier.create(channels)
                    .expectNext(getDefaultChannel())
                    .expectNext(getChannel())
                    .expectComplete()
                    .verify()
                StepVerifier.create(userChannels)
                    .expectComplete()
                    .verify()
            }
        }

        @Test
        fun `Create private channel without Principal`() {
            val exception = assertThrows<NotAuthorizedException> {
                channelService.createChannel(getChannel(true))
            }
            exception.message shouldBe PRIVATE_CHANNEL_WITHOUT_PRINCIPAL
        }
    }

}