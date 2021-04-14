package com.finances.notificationservice.repository

import com.finances.notificationservice.model.Channel
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChannelRepository: ReactiveMongoRepository<Channel, String> {

    fun deleteChannelByName(name: String): Mono<Void>

    fun findChannelByName(name: String): Flux<Channel>
}