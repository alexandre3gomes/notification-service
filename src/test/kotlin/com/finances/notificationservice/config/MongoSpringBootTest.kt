package com.finances.notificationservice.config

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@Target(AnnotationTarget.CLASS)
@SpringBootTest
@ContextConfiguration(initializers = [MongoInitializer::class])
annotation class MongoSpringBootTest