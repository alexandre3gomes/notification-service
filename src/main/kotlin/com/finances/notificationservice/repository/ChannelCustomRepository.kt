package com.finances.notificationservice.repository

import com.finances.notificationservice.model.Message
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.tail
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux


@Repository
class ChannelCustomRepository (private val template: ReactiveMongoTemplate) {

    fun createCollection(name: String, options: CollectionOptions) = template.createCollection(name, options)

    fun getMessagesByChannel(name: String): Flux<Message> = template.tail(Query(), name)

    fun collectionExists(name: String) = template.collectionExists(name)

    fun deleteCollection(name: String) = template.dropCollection(name)
}