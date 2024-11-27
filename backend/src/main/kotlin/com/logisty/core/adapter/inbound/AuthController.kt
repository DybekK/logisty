package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.toBadRequestResponseEntity
import com.logisty.core.adapter.toInternalServerErrorResponseEntity
import com.logisty.core.application.security.AuthService
import com.logisty.core.application.security.SecurityExceptions.InvalidTokenStructureException
import com.logisty.core.application.security.SecurityExceptions.TokenExpiredOrNotFoundException
import com.logisty.core.application.security.SecurityExceptions.UserBadCredentialsException
import com.logisty.core.application.security.jwt.values.JwtAccess
import com.logisty.core.application.security.jwt.values.JwtRefresh
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserPassword
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// authenticate user
data class AuthenticationRequest(
    val email: UserEmail,
    val password: UserPassword,
)

data class AuthenticationResponse(
    val accessToken: JwtAccess,
    val refreshToken: JwtRefresh,
)

// refresh token
data class RefreshTokenRequest(
    val refreshToken: JwtRefresh,
)

data class RefreshTokenResponse(
    val accessToken: JwtAccess,
    val refreshToken: JwtRefresh,
)

@RestController
@RequestMapping("api/auth")
class AuthController(
    private val authService: AuthService,
) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping
    fun authenticate(
        @RequestBody request: AuthenticationRequest,
    ) = runCatching { authService.authenticate(request.email, request.password) }
        .map { (accessToken, refreshToken) -> ResponseEntity.ok(AuthenticationResponse(accessToken, refreshToken)) }
        .getOrElse {
            when (it) {
                is UserBadCredentialsException -> it.toBadRequestResponseEntity()
                else -> it.toInternalServerErrorResponseEntity(logger)
            }
        }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody request: RefreshTokenRequest,
    ) = runCatching { authService.refresh(request.refreshToken) }
        .map { (accessToken, refreshToken) -> ResponseEntity.ok(RefreshTokenResponse(accessToken, refreshToken)) }
        .getOrElse {
            when (it) {
                is TokenExpiredOrNotFoundException,
                is InvalidTokenStructureException,
                -> it.toBadRequestResponseEntity()
                else -> it.toInternalServerErrorResponseEntity(logger)
            }
        }
}
