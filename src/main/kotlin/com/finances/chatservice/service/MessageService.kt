package com.finances.chatservice.service

import com.finances.chatservice.model.Message
import com.finances.chatservice.repository.MessageRepository
import org.springframework.stereotype.Service

@Service
class MessageService (private val repository: MessageRepository){

    fun saveMessage(message: Message) = repository.persistMessage(message).subscribe()

}
