package com.logisty.core.adapter.inbound.order

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.domain.ErrorCode
import com.logisty.core.domain.model.FixtureOrder
import com.logisty.core.domain.model.FixtureOrderStep
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.UserId
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration

class CreateOrderFunctionalTest : FunctionalTest() {
    @Test
    fun `should create order successfully`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val orderRequest = fixtures.order.toCreateOrderRequest()

        // when & then
        routes
            .createOrder(
                fixtures.fleet.fleetId,
                orderRequest,
                jwt,
            ).andExpect(status().isOk)
    }

    @Test
    fun `should return 400 when fleet is not found`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val orderRequest = fixtures.order.toCreateOrderRequest()

        // when & then
        routes
            .createOrder(
                FleetId.generate(),
                orderRequest,
                jwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.FLEET_NOT_FOUND)
    }

    @Test
    fun `should return 400 when driver is not found`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val orderRequest =
            fixtures
                .order
                .copy(driverId = UserId.generate())
                .toCreateOrderRequest()

        // when & then
        routes
            .createOrder(
                fixtures.fleet.fleetId,
                orderRequest,
                jwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `should return 400 when driver has invalid role`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val orderRequest =
            fixtures
                .order
                .copy(driverId = fixtures.dispatcher.userId)
                .toCreateOrderRequest()

        // when & then
        routes
            .createOrder(
                fixtures.fleet.fleetId,
                orderRequest,
                jwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.USER_IS_NOT_DRIVER)
    }

    @Test
    fun `should return 400 when dispatcher has invalid role`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val orderRequest =
            fixtures
                .order
                .copy(createdBy = fixtures.driver.userId)
                .toCreateOrderRequest()

        // when & then
        routes
            .createOrder(
                fixtures.fleet.fleetId,
                orderRequest,
                jwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.USER_IS_NOT_DISPATCHER)
    }

    @Test
    fun `should return 400 when step estimated arrival time is in the future`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val orderRequest = fixtures.order.invalidStepTimingsOrderRequest()

        // when & then
        routes
            .createOrder(
                fixtures.fleet.fleetId,
                orderRequest,
                jwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.STEP_ESTIMATED_ARRIVAL_TIME_IN_FUTURE)
    }

    @Test
    fun `should return 400 when order estimated start time is after end time`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val orderRequest = fixtures.order.invalidOrderTimingsOrderRequest()

        // when & then
        routes
            .createOrder(
                fixtures.fleet.fleetId,
                orderRequest,
                jwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.ORDER_ESTIMATED_START_TIME_AFTER_END_TIME)
    }

    private fun List<FixtureOrderStep>.copyInvalidSteps() =
        map { it.copy(estimatedArrivalAt = clock.instant().plus(Duration.ofHours(1))) }

    private fun FixtureOrder.invalidStepTimingsOrderRequest() =
        copy(steps = steps.copyInvalidSteps())
            .toCreateOrderRequest()

    private fun FixtureOrder.invalidOrderTimingsOrderRequest() =
        copy(
            estimatedStartedAt = clock.instant().plus(Duration.ofHours(1)),
            estimatedEndedAt = clock.instant(),
        ).toCreateOrderRequest()
}
