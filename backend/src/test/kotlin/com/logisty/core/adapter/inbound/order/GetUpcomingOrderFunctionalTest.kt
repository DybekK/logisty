package com.logisty.core.adapter.inbound.order

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.domain.model.values.UserId
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GetUpcomingOrderFunctionalTest : FunctionalTest() {
    @Test
    fun `should return upcoming order successfully`() {
        val (driverJwt, _) =
            routes.authenticateAndReturn(
                email = fixtures.driver.email,
                password = fixtures.driver.password,
            )

        routes
            .getUpcomingOrder(
                fleetId = fixtures.fleet.fleetId,
                driverId = fixtures.driver.userId,
                jwt = driverJwt,
            ).andExpect(status().isOk)
    }

    @Test
    fun `should return 404 when no upcoming order exists`() {
        val (driverJwt, _) =
            routes.authenticateAndReturn(
                email = fixtures.driver.email,
                password = fixtures.driver.password,
            )

        val differentDriverId = UserId.generate()

        routes
            .getUpcomingOrder(
                fleetId = fixtures.fleet.fleetId,
                driverId = differentDriverId,
                jwt = driverJwt,
            ).andExpect(status().isNotFound)
    }
}
