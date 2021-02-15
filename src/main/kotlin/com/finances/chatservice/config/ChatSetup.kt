package com.finances.chatservice.config

import com.finances.chatservice.model.Channel
import com.finances.chatservice.service.ChannelService
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ChatSetup (private val channelService: ChannelService) {

    @EventListener
    fun createDefaultChannel(event: ContextRefreshedEvent) {
        channelService.createChannel(Channel("general", "Default channel", false))
    }
}