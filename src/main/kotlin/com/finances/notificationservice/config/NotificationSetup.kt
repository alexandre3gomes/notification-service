package com.finances.notificationservice.config

import com.finances.notificationservice.model.Channel
import com.finances.notificationservice.service.ChannelService
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NotificationSetup (private val channelService: ChannelService) {

    @EventListener
    fun createDefaultChannel(event: ContextRefreshedEvent) {
        channelService.createChannel(Channel("general", "Default channel", false))
    }
}