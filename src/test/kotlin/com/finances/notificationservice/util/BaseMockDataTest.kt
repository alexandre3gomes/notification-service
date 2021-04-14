package com.finances.notificationservice.util

import com.finances.notificationservice.model.Channel
import com.finances.notificationservice.model.Message
import com.finances.notificationservice.model.UserChannel
import java.time.Instant
import org.springframework.security.oauth2.jwt.Jwt

object BaseMockDataTest {

    private const val MESSAGE_ID = "id"
    private const val MESSAGE = "Test Message"
    private const val FROM = "from@test.com"
    private const val TO = "to@test.com"
    private const val CHANNEL_ID = "id"
    private const val CHANNEL_DESCRIPTION = "Test description"
    const val CHANNEL_NAME = "test"
    const val ADMIN_EMAIL = "admin@test.com"

    fun getMessage(): Message {
        val msg = Message(MESSAGE, FROM, TO, CHANNEL_NAME)
        msg.id = MESSAGE_ID
        return msg
    }

    fun getChannel(isPrivate: Boolean = false): Channel {
        val channel = Channel(CHANNEL_NAME, CHANNEL_DESCRIPTION, isPrivate)
        channel.id = CHANNEL_ID
        return channel
    }

    fun getDefaultChannel() = Channel("general", "Default channel", false)

    fun getPrincipal(): Jwt {
        val tokenValue = "fakeToken"
        val issuedAt = Instant.now()
        val expiresAt = issuedAt.plusSeconds(3600)
        val header = mapOf(Pair("Authorization", "Bearer $tokenValue"))
        val claims = mapOf(Pair("email", ADMIN_EMAIL))
        return Jwt(tokenValue, issuedAt, expiresAt, header, claims)
    }

    fun getWrongPrincipal(): Jwt {
        val tokenValue = "fakeToken"
        val issuedAt = Instant.now()
        val expiresAt = issuedAt.plusSeconds(3600)
        val header = mapOf(Pair("Authorization", "Bearer $tokenValue"))
        val claims = mapOf(Pair("email", "wrong@mail.com"))
        return Jwt(tokenValue, issuedAt, expiresAt, header, claims)
    }

    fun getUserChannel(isPrivate: Boolean = false): UserChannel = UserChannel(ADMIN_EMAIL, listOf(getChannel(isPrivate)))
}