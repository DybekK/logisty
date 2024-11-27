package com.logisty.core.adapter.inbound.auth

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.application.MutableClock
import com.logisty.core.adapter.andExpectError
import com.logisty.core.application.security.SecurityErrorCode
import com.logisty.core.application.security.jwt.JwtService
import com.logisty.core.application.security.jwt.values.JwtRefresh
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration
import java.util.Date

class RefreshFunctionalTest : FunctionalTest() {
    @Autowired
    private lateinit var jwtService: JwtService

    @Test
    fun `should refresh token successfully`() {
        val (_, refreshJwt) = routes.authenticateAndReturn()

        // when & then
        routes
            .refresh(refreshJwt)
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 400 when refresh token is expired`() {
        val (_, refreshJwt) = routes.authenticateAndReturn()

        // when & then
        (clock as MutableClock).advanceBy(Duration.ofDays(2))
        routes
            .refresh(refreshJwt)
            .andExpect(status().isBadRequest)
            .andExpectError(SecurityErrorCode.TOKEN_EXPIRED_OR_NOT_FOUND)
    }

    @Test
    fun `should return 400 when token is reused`() {
        val (_, refreshJwt) = routes.authenticateAndReturn()
        (clock as MutableClock).advanceBy(Duration.ofSeconds(1))

        // when
        routes
            .refresh(refreshJwt)
            .andExpect(status().isOk)

        // when & then
        routes
            .refresh(refreshJwt)
            .andExpect(status().isBadRequest)
            .andExpectError(SecurityErrorCode.TOKEN_EXPIRED_OR_NOT_FOUND)
    }

    @Test
    fun `should return 400 when refresh token is invalid`() {
        // given
        val invalidRefreshJwt = generateInvalidRefreshToken()

        // when & then
        routes
            .refresh(invalidRefreshJwt)
            .andExpect(status().isBadRequest)
            .andExpectError(SecurityErrorCode.TOKEN_EXPIRED_OR_NOT_FOUND)
    }

    private fun generateInvalidRefreshToken(): JwtRefresh {
        val user =
            User
                .builder()
                .username("invalid-user@example.com")
                .password("invalid-password")
                .build()

        return jwtService
            .generate(user, Date(clock.millis() + Duration.ofDays(1).toMillis()))
            .let { JwtRefresh(it) }
    }
}
