package com.finances.notificationservice.service

import com.finances.notificationservice.model.Message
import com.finances.notificationservice.repository.MessageRepository
import org.springframework.stereotype.Service

@Service
class MessageService (private val repository: MessageRepository){

    fun saveMessage(message: Message) = repository.persistMessage(message).subscribe()

}
