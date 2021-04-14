package com.finances.notificationservice.util

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.security.Principal

object AuthUtils {

    fun extractMailFromPrincipal(principal: Principal): Mono<String> {
        return principal.toMono()
            .cast(JwtAuthenticationToken::class.java)
            .map(JwtAuthenticationToken::getCredentials)
            .cast(Jwt::class.java)
            .map(Jwt::getClaims)
            .map { it["email"] }
            .cast(String::class.java)
    }

}