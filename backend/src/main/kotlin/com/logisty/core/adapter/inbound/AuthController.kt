package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.toBadRequestResponseEntity
import com.logisty.core.adapter.toInternalServerErrorResponseEntity
import com.logisty.core.application.security.AuthService
import com.logisty.core.application.security.SecurityExceptions.InvalidTokenStructureException
import com.logisty.core.application.security.SecurityExceptions.TokenExpiredOrNotFoundException
import com.logisty.core.application.security.SecurityExceptions.UserBadCredentialsException
import com.logisty.core.application.security.jwt.values.JwtAccess
import com.logisty.core.application.security.jwt.values.JwtRefresh
import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.model.values.UserRole
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// get current user
data class GetCurrentUserResponse(
    val userId: UserId,
    val fleetId: FleetId,
    val email: UserEmail,
    val firstName: FirstName,
    val lastName: LastName,
    val roles: List<UserRole>,
)

fun User.toGetCurrentUserResponse() =
    GetCurrentUserResponse(
        userId = userId,
        fleetId = fleetId,
        email = email,
        firstName = firstName,
        lastName = lastName,
        roles = roles,
    )

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

    @GetMapping("/me")
    fun getCurrentUser(
        @RequestHeader("Authorization") authorizationHeader: String,
    ) = runCatching { authService.getCurrentUser(authorizationHeader) }
        .map { ResponseEntity.ok(it.toGetCurrentUserResponse()) }
        .getOrElse {
            when (it) {
                is UserBadCredentialsException -> it.toBadRequestResponseEntity()
                else -> it.toInternalServerErrorResponseEntity(logger)
            }
        }

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
