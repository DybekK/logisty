package com.logisty.core.adapter.inbound.order

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.adapter.inbound.ReportOrderRequest
import com.logisty.core.domain.ErrorCode
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderStepId
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ReportOrderFunctionalTest : FunctionalTest() {
    @Test
    fun `should report order step successfully`() {
        val (driverJwt, _) =
            routes.authenticateAndReturn(
                email = fixtures.driver.email,
                password = fixtures.driver.password,
            )
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

    @Test
    fun `should return 400 when order is not found`() {
        val (driverJwt, _) =
            routes.authenticateAndReturn(
                email = fixtures.driver.email,
                password = fixtures.driver.password,
            )
        val request =
            ReportOrderRequest(
                arrivedAt = clock.instant(),
                lat = 52.237049,
                lon = 21.017532,
            )

        routes
            .reportOrder(
                fleetId = fixtures.fleet.fleetId,
                orderId = OrderId.generate(),
                stepId =
                    fixtures.order.steps
                        .first()
                        .orderStepId,
                request = request,
                jwt = driverJwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.ORDER_NOT_FOUND)
    }

    @Test
    fun `should return 400 when step is not found`() {
        val (driverJwt, _) =
            routes.authenticateAndReturn(
                email = fixtures.driver.email,
                password = fixtures.driver.password,
            )
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
                stepId = OrderStepId.generate(),
                request = request,
                jwt = driverJwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.ORDER_STEP_NOT_FOUND)
    }

    @Test
    fun `should return 400 when reporting steps out of sequence`() {
        val (driverJwt, _) =
            routes.authenticateAndReturn(
                email = fixtures.driver.email,
                password = fixtures.driver.password,
            )
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
                        .last()
                        .orderStepId,
                request = request,
                jwt = driverJwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.ORDER_STEP_INVALID_SEQUENCE)
    }
}
