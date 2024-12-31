package com.logisty.core.adapter.inbound.auth

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.adapter.inbound.CreateFleetRequest
import com.logisty.core.application.MutableClock
import com.logisty.core.application.security.SecurityErrorCode
import com.logisty.core.application.security.jwt.JwtService
import com.logisty.core.application.security.jwt.values.JwtAccess
import com.logisty.core.domain.model.values.FleetName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration
import java.util.Date

class SecurityFunctionalTest : FunctionalTest() {
    @Autowired
    private lateinit var jwtService: JwtService

    @Test
    fun `should not filter unprotected routes`() {
        // when & then
        routes
            .authenticate(
                fixtures.user.email,
                fixtures.user.password,
            ).andExpect(status().isOk)
    }

    @Test
    fun `should authenticate protected routes`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val request = CreateFleetRequest(FleetName("Test Fleet"))

        // when & then
        routes
            .createFleet(request, jwt)
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 401 when token is not present`() {
        // given
        val request = CreateFleetRequest(FleetName("Test Fleet"))

        // when & then
        routes
            .createFleet(request)
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should return 401 when token has invalid structure`() {
        // given
        val invalidJwt = JwtAccess("invalid-jwt")
        val request = CreateFleetRequest(FleetName("Test Fleet"))

        // when & then
        routes
            .createFleet(request, invalidJwt)
            .andExpect(status().isUnauthorized)
            .andExpectError(SecurityErrorCode.INVALID_TOKEN_STRUCTURE)
    }

    @Test
    fun `should return 401 when user is not found`() {
        // given
        val invalidJwt = generateInvalidJwt()
        val request = CreateFleetRequest(FleetName("Test Fleet"))

        // when & then
        routes
            .createFleet(request, invalidJwt)
            .andExpect(status().isUnauthorized)
            .andExpectError(SecurityErrorCode.TOKEN_EXPIRED_OR_NOT_FOUND)
    }

    @Test
    fun `should return 401 when token is expired`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val request = CreateFleetRequest(FleetName("Test Fleet"))

        // when & then
        (clock as MutableClock).advanceBy(Duration.ofDays(15))
        routes
            .createFleet(request, jwt)
            .andExpect(status().isUnauthorized)
            .andExpectError(SecurityErrorCode.TOKEN_EXPIRED_OR_NOT_FOUND)
    }

    private fun generateInvalidJwt(): JwtAccess {
        val user =
            User
                .builder()
                .username("invalid-user@example.com")
                .password("invalid-password")
                .build()

        return jwtService
            .generate(user, Date(clock.millis() + Duration.ofDays(1).toMillis()))
            .let { JwtAccess(it) }
    }
}
