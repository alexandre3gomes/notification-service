package com.finances.chatservice.model

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Message (val message: String, val from: String, val to: String, val channel: String){

    lateinit var id: String
}