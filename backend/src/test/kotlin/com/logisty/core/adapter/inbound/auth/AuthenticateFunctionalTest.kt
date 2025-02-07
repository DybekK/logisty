package com.logisty.core.adapter.inbound.auth

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.application.security.SecurityErrorCode
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserPassword
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthenticateFunctionalTest : FunctionalTest() {
    @Test
    fun `should authenticate successfully`() {
        // when & then
        routes
            .authenticate(
                fixtures.dispatcher.email,
                fixtures.dispatcher.password,
            ).andExpect(status().isOk)
    }

    @Test
    fun `should return 400 when user not found`() {
        // given
        val invalidUser = fixtures.dispatcher.copy(email = UserEmail("test2@example.com"))

        // when & then
        routes
            .authenticate(
                invalidUser.email,
                invalidUser.password,
            ).andExpect(status().isBadRequest)
            .andExpectError(SecurityErrorCode.BAD_CREDENTIALS)
    }

    @Test
    fun `should return 400 when password is incorrect`() {
        // given
        val invalidUser = fixtures.dispatcher.copy(password = UserPassword("password2"))

        // when & then
        routes
            .authenticate(
                invalidUser.email,
                invalidUser.password,
            ).andExpect(status().isBadRequest)
            .andExpectError(SecurityErrorCode.BAD_CREDENTIALS)
    }
}
