package com.finances.notificationservice

import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories
class notificationServiceApplication

fun main(args: Array<String>) {
	runApplication<notificationServiceApplication>(*args)
}

@Bean
fun getTemplate() = ReactiveMongoTemplate(MongoClients.create(), "notification")
