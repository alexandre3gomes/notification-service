package com.finances.notificationservice.unit.service

import com.finances.notificationservice.repository.ChannelCustomRepository
import com.finances.notificationservice.repository.ChannelRepository
import com.finances.notificationservice.repository.UserChannelRepository
import com.finances.notificationservice.service.ChannelService
import com.finances.notificationservice.util.BaseMockDataTest.CHANNEL_NAME
import com.finances.notificationservice.util.BaseMockDataTest.getChannel
import com.finances.notificationservice.util.BaseMockDataTest.getMessage
import com.finances.notificationservice.util.BaseMockDataTest.getPrincipal
import com.finances.notificationservice.util.BaseMockDataTest.getUserChannel
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig(ChannelService::class)
class ChannelServiceTest (@Autowired private val service: ChannelService) {

    @MockkBean
    lateinit var repository: ChannelRepository

    @MockkBean
    lateinit var customRepository: ChannelCustomRepository

    @MockkBean
    lateinit var userChannelRepository: UserChannelRepository

    @Test
    fun `Create channel without principal`() {
        every { repository.save(any()) } returns Mono.just(getChannel())
        every { customRepository.createCollection(any(), any()) } returns Mono.empty()
        every { customRepository.collectionExists(any()) } returns Mono.just(false)
        service.createChannel(getChannel())
        verify(exactly = 1){ customRepository.collectionExists(any()) }
        verify(exactly = 1){ customRepository.createCollection(any(), any()) }
        verify(exactly = 1){ repository.save(any()) }
        verify(exactly = 0){ userChannelRepository.save(any()) }
    }

    @Test
    fun `Delete channel`() {
        every { repository.deleteChannelByName(any()) } returns Mono.empty()
        every { customRepository.deleteCollection(any())}  returns Mono.empty()
        service.deleteChannel(CHANNEL_NAME)
        verify(exactly = 1) { repository.deleteChannelByName(any()) }
        verify(exactly = 1) { customRepository.deleteCollection(any()) }
    }

    @Test
    fun `Get messages by channel`() {
        every { customRepository.getMessagesByChannel(any()) } returns Flux.just(getMessage())
        every { repository.findChannelByName(any()) } returns Flux.just(getChannel())
        service.getMessagesByChannel(CHANNEL_NAME, JwtAuthenticationToken(getPrincipal()))
        verify(exactly = 1) { repository.findChannelByName(any()) }
    }

    @Test
    fun `List channels`() {
        every { userChannelRepository.findAllByUser(any()) } returns Flux.just(getUserChannel())
        service.listChannels(JwtAuthenticationToken(getPrincipal()))
        verify(exactly = 1) { userChannelRepository.findAllByUser(any())}
    }
}