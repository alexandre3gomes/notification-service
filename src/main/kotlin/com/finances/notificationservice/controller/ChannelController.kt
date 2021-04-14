package com.finances.notificationservice.controller

import com.finances.notificationservice.exception.NotAuthorizedException
import com.finances.notificationservice.model.Channel
import com.finances.notificationservice.service.ChannelService
import org.springframework.http.HttpStatus
import java.security.Principal
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/channel")
class ChannelController(val service: ChannelService) {

    @PostMapping("/")
    fun createChannel(@RequestBody channel: Channel, principal: Principal) {
        try {
            service.createChannel(channel, principal)
        } catch (e: NotAuthorizedException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, e.message, e)
        }
    }

    @GetMapping("/{channel}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getMessages(@PathVariable channel: String, principal: Principal) = service.getMessagesByChannel(channel, principal)

}