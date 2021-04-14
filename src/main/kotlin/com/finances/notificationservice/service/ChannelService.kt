package com.finances.notificationservice.service

import com.finances.notificationservice.exception.ExceptionMessages.PRIVATE_CHANNEL_WITHOUT_PRINCIPAL
import com.finances.notificationservice.exception.ExceptionMessages.USER_HAS_NO_ACCESS
import com.finances.notificationservice.exception.NotAuthorizedException
import com.finances.notificationservice.model.Channel
import com.finances.notificationservice.model.Message
import com.finances.notificationservice.model.UserChannel
import com.finances.notificationservice.repository.ChannelCustomRepository
import com.finances.notificationservice.repository.ChannelRepository
import com.finances.notificationservice.repository.UserChannelRepository
import com.finances.notificationservice.util.AuthUtils.extractMailFromPrincipal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.security.Principal

@Service
class ChannelService(
    private val customRepository: ChannelCustomRepository,
    private val repository: ChannelRepository,
    private val userChannelRepository: UserChannelRepository
) {

    fun createChannel(channel: Channel, principal: Principal? = null) {
        if (channel.private && principal == null) {
            throw NotAuthorizedException(PRIVATE_CHANNEL_WITHOUT_PRINCIPAL)
        }
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

    fun deleteChannel(name: String) {
        customRepository.deleteCollection(name).subscribe()
        repository.deleteChannelByName(name).subscribe()
    }

    fun getMessagesByChannel(name: String, principal: Principal): Flux<Message> {
        return repository.findChannelByName(name)
            .flatMap {
                if (it.private) {
                    getMessagesOfPrivateChannel(it, principal)
                } else {
                    getMessagesOfPublicChannel(it)
                }
            }
    }

    fun listChannels(principal: Principal): Flux<UserChannel> = userChannelRepository.findAllByUser(extractMailFromPrincipal(principal))

    private fun addUserChannel(channel: Channel, principal: Principal) {
        extractMailFromPrincipal(principal).subscribe {
            userChannelRepository.save(UserChannel(it, listOf(channel))).subscribe()
        }
    }

    private fun getMessagesOfPublicChannel(channel: Channel): Flux<Message> {
        return Flux.just(true)
            .filter { it }
            .publish { customRepository.getMessagesByChannel(channel.name) }
    }

    private fun getMessagesOfPrivateChannel(channel: Channel, principal: Principal): Flux<Message> {
        return userChannelRepository.findAllByUser(extractMailFromPrincipal(principal))
            .log()
            .map(UserChannel::channels)
            .filter { it.contains(channel) }
            .switchIfEmpty(Flux.error(NotAuthorizedException(USER_HAS_NO_ACCESS.format(channel.name))))
            .flatMap { customRepository.getMessagesByChannel(channel.name) }
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(ChannelService::class.java)
    }
}