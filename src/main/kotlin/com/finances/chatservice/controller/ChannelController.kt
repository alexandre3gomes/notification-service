package com.finances.chatservice.controller

import com.finances.chatservice.model.Channel
import com.finances.chatservice.service.ChannelService
import java.security.Principal
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/channel")
class ChannelController(val service: ChannelService) {

    @PostMapping("/")
    fun createChannel(@RequestBody channel: Channel, principal: Principal) = service.createChannel(channel, principal)

    @GetMapping("/{channel}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getMessages(@PathVariable channel: String) = service.getMessagesByChannel(channel)

}