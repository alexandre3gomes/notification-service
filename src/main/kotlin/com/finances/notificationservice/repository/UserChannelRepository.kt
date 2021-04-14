package com.finances.notificationservice.repository

import com.finances.notificationservice.model.UserChannel
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserChannelRepository: ReactiveMongoRepository<UserChannel, String> {

    fun findAllByUser(user: Mono<String>): Flux<UserChannel>
}