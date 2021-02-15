package com.finances.chatservice.service

import com.finances.chatservice.model.Channel
import com.finances.chatservice.model.Message
import com.finances.chatservice.model.UserChannel
import com.finances.chatservice.repository.ChannelCustomRepository
import com.finances.chatservice.repository.ChannelRepository
import com.finances.chatservice.repository.UserChannelRepository
import java.security.Principal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class ChannelService(
    private val customRepository: ChannelCustomRepository,
    private val repository: ChannelRepository,
    private val userChannelRepository: UserChannelRepository
) {

    fun createChannel(channel: Channel, principal: Principal? = null) {
        customRepository.collectionExists(channel.name).subscribe {
            if (!it) {
                val options = CollectionOptions.empty()
                    .capped().size(124000).maxDocuments(5000)
                customRepository.createCollection(channel.name, options).subscribe()
                repository.save(channel).subscribe()
                if (principal != null) {
                    addUserChannel(channel, principal)
                }
            } else {
                LOGGER.info("Collection ${channel.name} already exists")
            }
        }
    }

    fun deleteChannel(name: String) = customRepository.deleteCollection(name).subscribe()

    fun getMessagesByChannel(name: String): Flux<Message> {
        return customRepository.getMessagesByChannel(name)
    }

    private fun addUserChannel(channel: Channel, principal: Principal) {
        extractMail(principal).subscribe {
            userChannelRepository.save(UserChannel(it, listOf(channel))).subscribe()
        }
    }

    fun listChannel(principal: Principal): Flux<UserChannel> = userChannelRepository.findAllByUser(extractMail(principal))


    private fun extractMail(principal: Principal): Mono<String> {
        return principal.toMono()
            .cast(JwtAuthenticationToken::class.java)
            .map(JwtAuthenticationToken::getCredentials)
            .cast(Jwt::class.java)
            .map(Jwt::getClaims)
            .map { it["email"] }
            .cast(String::class.java)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(ChannelService::class.java)
    }
}