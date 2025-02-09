package com.logisty.core.adapter.inbound.order

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andReturnResponse
import com.logisty.core.adapter.inbound.GetOrdersResponse
import com.logisty.core.application.security.jwt.values.JwtAccess
import com.logisty.core.domain.model.query.GetOrdersQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GetOrdersFunctionalTest : FunctionalTest() {
    @Test
    fun `should get orders`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val limit = 10
        val page = 0
        val orders = generateOrders(limit + 10, jwt)

        // when
        val response =
            routes
                .getOrders(GetOrdersQuery(fixtures.fleet.fleetId, page, limit), jwt)
                .andExpect(status().isOk)
                .andReturnResponse<GetOrdersResponse>()

        // then
        assertThat(response.orders).hasSize(limit)
        assertThat(response.total).isEqualTo(orders.size.toLong())
    }

    @Test
    fun `should get orders with pagination`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val limit = 5
        val page = 2
        val orders = generateOrders(limit + 10, jwt)

        // when
        val response =
            routes
                .getOrders(GetOrdersQuery(fixtures.fleet.fleetId, page, limit), jwt)
                .andExpect(status().isOk)
                .andReturnResponse<GetOrdersResponse>()

        // then
        val expectedSize = minOf(limit, maxOf(0, orders.size - page * limit))
        assertThat(response.orders).hasSize(expectedSize)
        assertThat(response.total).isEqualTo(orders.size.toLong())
    }

    private fun generateOrders(
        limit: Int,
        jwt: JwtAccess,
    ) = List(limit) { fixtures.order.toCreateOrderRequest() }
        .map { routes.createOrder(fixtures.fleet.fleetId, it, jwt) }
}
