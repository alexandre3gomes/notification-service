package com.finances.chatservice.repository

import com.finances.chatservice.model.Message
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository

@Repository
class MessageRepository (private val template: ReactiveMongoTemplate){

    fun persistMessage(message: Message) = template.save(message, message.channel)

}
