package com.logisty.core.application.security

import com.logisty.core.application.security.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider,
) {
    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration =
            CorsConfiguration().apply {
                allowedOriginPatterns = listOf("*") // More permissive for testing
                allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                allowedHeaders =
                    listOf(
                        "Authorization",
                        "Content-Type",
                        "X-Requested-With",
                        "Accept",
                        "Origin",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers",
                    )
                exposedHeaders =
                    listOf(
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Credentials",
                        "Authorization",
                    )
                allowCredentials = true
                maxAge = 3600L
            }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter,
    ): DefaultSecurityFilterChain {
        http
            .cors {}
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/api/auth",
                        "/api/auth/refresh",
                        "/api/fleets/create", // only for staging
                        "/api/fleets/invitations/*",
                        "/api/fleets/invitations/*/accept",
                    ).permitAll()
                    .anyRequest()
                    .authenticated()
            }.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { it.authenticationEntryPoint { _, response, _ -> response.sendError(401) } }

        return http.build()
    }
}
