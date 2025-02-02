package com.logisty.core.application.security

import com.logisty.core.application.security.SecurityExceptions.InvalidTokenStructureException
import com.logisty.core.application.security.SecurityExceptions.TokenExpiredOrNotFoundException
import com.logisty.core.application.security.SecurityExceptions.UserBadCredentialsException
import com.logisty.core.application.security.jwt.JwtService
import com.logisty.core.application.security.jwt.JwtStorage
import com.logisty.core.application.security.jwt.values.JwtAccess
import com.logisty.core.application.security.jwt.values.JwtProperties
import com.logisty.core.application.security.jwt.values.JwtRefresh
import com.logisty.core.application.security.jwt.values.extractTokenValue
import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.port.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.Date

@Service
@Transactional
class AuthService(
    private val clock: Clock,
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val userRepository: UserRepository,
    private val jwtStorage: JwtStorage,
    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,
) {
    fun getCurrentUser(authorizationHeader: String): User =
        authorizationHeader
            .extractTokenValue()
            .let { jwtService.extractEmail(it) }
            ?.let { userRepository.findByEmail(it) }
            ?: throw UserBadCredentialsException()

    fun authenticate(
        email: UserEmail,
        password: UserPassword,
    ): Pair<JwtAccess, JwtRefresh> {
        runCatching {
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    email.value,
                    password.value,
                ),
            )
        }.onFailure { throw UserBadCredentialsException() }

        return userDetailsService
            .loadUserByUsername(email.value)
            .let(::generateAndSaveTokens)
    }

    fun refresh(jwtRefresh: JwtRefresh): Pair<JwtAccess, JwtRefresh> {
        val email = jwtService.extractEmail(jwtRefresh) ?: throw InvalidTokenStructureException()
        val user = jwtStorage.findUserDetailsByToken(jwtRefresh) ?: throw TokenExpiredOrNotFoundException()
        val currentUserDetails = userDetailsService.loadUserByUsername(email.value)

        return if (jwtService.isExpired(jwtRefresh) || user.username != currentUserDetails.username) {
            jwtStorage.remove(jwtRefresh)
            throw TokenExpiredOrNotFoundException()
        } else {
            jwtStorage.remove(jwtRefresh)
            generateAndSaveTokens(user)
        }
    }

    private fun generateAndSaveTokens(user: UserDetails): Pair<JwtAccess, JwtRefresh> {
        val accessToken = generateAccessToken(user)
        val refreshToken = generateRefreshToken(user)
        jwtStorage.save(refreshToken, user)

        return accessToken to refreshToken
    }

    private fun generateAccessToken(user: UserDetails) =
        jwtService
            .generate(
                userDetails = user,
                expirationDate = Date(clock.millis() + jwtProperties.accessTokenExpiration),
            ).let { JwtAccess(it) }

    fun generateRefreshToken(user: UserDetails) =
        jwtService
            .generate(
                userDetails = user,
                expirationDate = Date(clock.millis() + jwtProperties.refreshTokenExpiration),
            ).let { JwtRefresh(it) }
}
