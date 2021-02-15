package com.finances.chatservice.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
class SpringSecurityConfig() {

    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.authorizeExchange()
            .matchers(PUBLIC_URLS)
            .permitAll()
            .matchers(PROTECTED_URLS)
            .authenticated()
            .and()
            .oauth2Login()
            .and()
            .oauth2ResourceServer()
            .jwt()
        return http.build()
    }

    companion object {
        private val PUBLIC_URLS = ServerWebExchangeMatchers.pathMatchers("/public/**",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/webjars/**",
            "/configuration/**",
            "/swagger-resources/**",
            "/actuator/**")
        private val PROTECTED_URLS = NegatedServerWebExchangeMatcher(PUBLIC_URLS)
    }
}
