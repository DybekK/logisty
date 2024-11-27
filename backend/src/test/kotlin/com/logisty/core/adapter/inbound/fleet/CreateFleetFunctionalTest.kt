package com.logisty.core.adapter.inbound.fleet

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.adapter.inbound.CreateFleetRequest
import com.logisty.core.domain.ErrorCode
import com.logisty.core.domain.model.values.FleetName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CreateFleetFunctionalTest : FunctionalTest() {
    @Test
    fun `should create fleet successfully`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val request = CreateFleetRequest(FleetName("Test Fleet"))

        // when & then
        routes
            .createFleet(request, jwt)
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 400 when fleet already exists`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val request = CreateFleetRequest(FleetName("Duplicate Fleet"))

        routes
            .createFleet(request, jwt)
            .andExpect(status().isOk)

        // when & then
        routes
            .createFleet(request, jwt)
            .andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.FLEET_ALREADY_EXISTS)
    }
}
