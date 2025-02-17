package com.logisty.core.adapter.inbound.order

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.adapter.inbound.ReportOrderRequest
import com.logisty.core.adapter.inbound.TrackDriverLocationRequest
import com.logisty.core.application.security.jwt.values.JwtAccess
import com.logisty.core.domain.ErrorCode
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import org.junit.jupiter.api.Test
import org.postgis.LineString
import org.postgis.Point
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TrackDriverLocationFunctionalTest : FunctionalTest() {
    @Test
    fun `should track driver location successfully`() {
        val (driverJwt, _) =
            routes.authenticateAndReturn(
                email = fixtures.driver.email,
                password = fixtures.driver.password,
            )

        // given
        val request =
            TrackDriverLocationRequest(
                route =
                    LineString(
                        arrayOf(
                            Point(52.237049, 21.017532),
                            Point(52.237049, 21.017532),
                        ),
                    ),
            )

        // when
        startOrder(driverJwt)

        // when & then
        routes
            .trackDriverLocation(
                fleetId = fixtures.fleet.fleetId,
                orderId = fixtures.order.orderId,
                request = request,
                jwt = driverJwt,
            ).andExpect(status().isOk)
    }

    @Test
    fun `should return 400 when fleet is not found`() {
        val (driverJwt, _) =
            routes.authenticateAndReturn(
                email = fixtures.driver.email,
                password = fixtures.driver.password,
            )

        // given
        val request =
            TrackDriverLocationRequest(
                route =
                    LineString(
                        arrayOf(Point(52.237049, 21.017532), Point(52.237049, 21.017532)),
                    ),
            )

        // when & then
        routes
            .trackDriverLocation(
                fleetId = FleetId.generate(),
                orderId = fixtures.order.orderId,
                request = request,
                jwt = driverJwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.FLEET_NOT_FOUND)
    }

    @Test
    fun `should return 400 when order is not found`() {
        val (driverJwt, _) =
            routes.authenticateAndReturn(
                email = fixtures.driver.email,
                password = fixtures.driver.password,
            )

        // given
        val request =
            TrackDriverLocationRequest(
                route =
                    LineString(
                        arrayOf(Point(52.237049, 21.017532), Point(52.237049, 21.017532)),
                    ),
            )

        // when & then
        routes
            .trackDriverLocation(
                fleetId = fixtures.fleet.fleetId,
                orderId = OrderId.generate(),
                request = request,
                jwt = driverJwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.ORDER_NOT_FOUND)
    }

    @Test
    fun `should return 400 when order is not pending`() {
        val (driverJwt, _) =
            routes.authenticateAndReturn(
                email = fixtures.driver.email,
                password = fixtures.driver.password,
            )

        // given
        val request =
            TrackDriverLocationRequest(
                route =
                    LineString(
                        arrayOf(Point(52.237049, 21.017532), Point(52.237049, 21.017532)),
                    ),
            )

        // when & then
        routes
            .trackDriverLocation(
                fleetId = fixtures.fleet.fleetId,
                orderId = fixtures.order.orderId,
                request = request,
                jwt = driverJwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.CANNOT_TRACK_DRIVER_LOCATION)
    }

    private fun startOrder(driverJwt: JwtAccess) {
        val request =
            ReportOrderRequest(
                arrivedAt = clock.instant(),
                lat = 52.237049,
                lon = 21.017532,
            )

        routes
            .reportOrder(
                fleetId = fixtures.fleet.fleetId,
                orderId = fixtures.order.orderId,
                stepId =
                    fixtures.order.steps
                        .first()
                        .orderStepId,
                request = request,
                jwt = driverJwt,
            ).andExpect(status().isOk)
    }
}
