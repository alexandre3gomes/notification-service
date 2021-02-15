package com.finances.chatservice.repository

import com.finances.chatservice.model.UserChannel
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserChannelRepository: ReactiveMongoRepository<UserChannel, String> {

    fun findAllByUser(user: Mono<String>): Flux<UserChannel>
}