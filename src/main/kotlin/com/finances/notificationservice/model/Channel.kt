package com.finances.notificationservice.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "channels")
data class Channel (val name: String, val description: String, val private: Boolean) {

    lateinit var id: String
}