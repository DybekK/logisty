package com.logisty.core.adapter.inbound.order

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andReturnResponse
import com.logisty.core.adapter.inbound.GetAvailableDriversResponse
import com.logisty.core.application.security.jwt.values.JwtAccess
import com.logisty.core.domain.model.query.GetAvailableDriversQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration
import java.time.Instant

class GetAvailableDriversFunctionalTest : FunctionalTest() {
    @Test
    fun `should get available drivers when requested time does not overlap with existing order`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val startAt = fixtures.order.estimatedEndedAt.plus(Duration.ofHours(1))
        val endAt = startAt.plus(Duration.ofHours(1))

        // when & then
        generateOrder(jwt)

        val response =
            routes
                .getAvailableDrivers(
                    GetAvailableDriversQuery(
                        fleetId = fixtures.fleet.fleetId,
                        startAt = startAt,
                        endAt = endAt,
                    ),
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<GetAvailableDriversResponse>()

        // then
        assertThat(response.drivers).hasSize(1)
    }

    @Test
    fun `should not get available drivers when requested time partially overlaps with existing order from the end`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val orderStartAt = fixtures.order.estimatedEndedAt.plus(Duration.ofHours(1))
        val orderEndAt = orderStartAt.plus(Duration.ofHours(1))
        generateOrder(jwt, orderStartAt, orderEndAt)

        // when & then
        val requestStartAt = orderStartAt.plus(Duration.ofMinutes(30))
        val requestEndAt = requestStartAt.plus(Duration.ofHours(1))

        val response =
            routes
                .getAvailableDrivers(
                    GetAvailableDriversQuery(
                        fleetId = fixtures.fleet.fleetId,
                        startAt = requestStartAt,
                        endAt = requestEndAt,
                    ),
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<GetAvailableDriversResponse>()

        assertThat(response.drivers).isEmpty()
    }

    @Test
    fun `should not get available drivers when requested time partially overlaps with existing order from start`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val orderStartAt = fixtures.order.estimatedEndedAt.plus(Duration.ofHours(1))
        val orderEndAt = orderStartAt.plus(Duration.ofHours(1))
        generateOrder(jwt, orderStartAt, orderEndAt)

        // when & then
        val requestStartAt = orderStartAt.minus(Duration.ofMinutes(30))
        val requestEndAt = orderStartAt.plus(Duration.ofMinutes(30))

        val response =
            routes
                .getAvailableDrivers(
                    GetAvailableDriversQuery(
                        fleetId = fixtures.fleet.fleetId,
                        startAt = requestStartAt,
                        endAt = requestEndAt,
                    ),
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<GetAvailableDriversResponse>()

        assertThat(response.drivers).isEmpty()
    }

    @Test
    fun `should not get available drivers when requested time completely overlaps with existing order`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val orderStartAt = fixtures.order.estimatedEndedAt.plus(Duration.ofHours(1))
        val orderEndAt = orderStartAt.plus(Duration.ofHours(1))
        generateOrder(jwt, orderStartAt, orderEndAt)

        // when & then
        val requestStartAt = orderStartAt.minus(Duration.ofMinutes(30))
        val requestEndAt = orderEndAt.plus(Duration.ofMinutes(30))

        val response =
            routes
                .getAvailableDrivers(
                    GetAvailableDriversQuery(
                        fleetId = fixtures.fleet.fleetId,
                        startAt = requestStartAt,
                        endAt = requestEndAt,
                    ),
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<GetAvailableDriversResponse>()

        assertThat(response.drivers).isEmpty()
    }

    private fun generateOrder(
        jwt: JwtAccess,
        startAt: Instant = clock.instant(),
        endAt: Instant = startAt.plus(Duration.ofHours(1)),
    ) = routes.createOrder(
        fleetId = fixtures.fleet.fleetId,
        request =
            fixtures.order
                .copy(
                    estimatedStartedAt = startAt,
                    estimatedEndedAt = endAt,
                ).toCreateOrderRequest(),
        jwt = jwt,
    )
}
