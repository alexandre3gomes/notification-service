package com.finances.chatservice.controller

import com.finances.chatservice.model.Message
import com.finances.chatservice.service.MessageService
import java.security.Principal
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/message")
class MessageController (val service: MessageService){

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun postMessage(@RequestBody message: Message) = service.saveMessage(message)
}