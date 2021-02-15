package com.finances.chatservice.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "userChannels")
data class UserChannel  (val user: String, val channels: List<Channel> = mutableListOf()) {

    lateinit var id: String
}