package com.logisty.core.adapter.inbound.auth

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.application.security.jwt.JwtService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GetCurrentUserFunctionalTest : FunctionalTest() {
    @Autowired
    private lateinit var jwtService: JwtService

    @Test
    fun `should get current user successfully`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // when & then
        routes
            .getCurrentUser(jwt)
            .andExpect(status().isOk)
    }
}
