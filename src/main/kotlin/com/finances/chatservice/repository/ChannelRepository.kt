package com.finances.chatservice.repository

import com.finances.chatservice.model.Channel
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ChannelRepository: ReactiveMongoRepository<Channel, String>