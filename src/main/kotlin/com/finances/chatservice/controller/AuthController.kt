package com.finances.chatservice.controller

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController {

    @GetMapping("/token")
    fun test(): Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
            .switchIfEmpty(Mono.error(IllegalStateException("ReactiveSecurityContext is empty")))
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getPrincipal)
            .cast(DefaultOidcUser::class.java)
            .map(DefaultOidcUser::getIdToken)
            .map(OidcIdToken::getTokenValue)
    }
}