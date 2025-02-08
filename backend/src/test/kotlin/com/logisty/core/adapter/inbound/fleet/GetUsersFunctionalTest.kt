package com.logisty.core.adapter.inbound.fleet

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andReturnResponse
import com.logisty.core.adapter.inbound.AcceptInvitationRequest
import com.logisty.core.adapter.inbound.GetUsersResponse
import com.logisty.core.application.security.jwt.values.JwtAccess
import com.logisty.core.domain.model.FixtureInvitation
import com.logisty.core.domain.model.query.GetUsersQuery
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.model.values.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GetUsersFunctionalTest : FunctionalTest() {
    @Test
    fun `should get users`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val limit = 10
        val page = 0
        val users = generateUsers(limit, jwt)
        val usersSize = users.size + 2

        // when
        val response =
            routes
                .getUsers(
                    GetUsersQuery(
                        fleetId = fixtures.fleet.fleetId,
                        limit = limit,
                        page = page,
                    ),
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<GetUsersResponse>()

        // then
        assertThat(response.users).hasSize(limit)
        assertThat(response.total).isEqualTo(usersSize.toLong())
    }

    @Test
    fun `should get users with pagination`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val limit = 10
        val page = 2
        val users = generateUsers(limit, jwt)
        val usersSize = users.size + 2

        // when
        val response =
            routes
                .getUsers(
                    GetUsersQuery(
                        fleetId = fixtures.fleet.fleetId,
                        limit = limit,
                        page = page,
                    ),
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<GetUsersResponse>()

        // then
        val expectedSize = minOf(limit, maxOf(0, usersSize - page * limit))
        assertThat(response.users).hasSize(expectedSize)
        assertThat(response.total).isEqualTo(usersSize.toLong())
    }

    @Test
    fun `should get users with role`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val limit = 10
        val page = 0
        generateUsers(limit, jwt)

        // when
        val response =
            routes
                .getUsers(
                    GetUsersQuery(
                        fleetId = fixtures.fleet.fleetId,
                        limit = limit,
                        page = page,
                        role = UserRole.DISPATCHER,
                    ),
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<GetUsersResponse>()

        // then
        assertThat(response.users).hasSize(limit)
        assertThat(response.total).isEqualTo(11L)
    }

    @Test
    fun `should get users with email`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val limit = 10
        val page = 0
        generateUsers(limit, jwt)

        // when
        val response =
            routes
                .getUsers(
                    GetUsersQuery(
                        fleetId = fixtures.fleet.fleetId,
                        limit = limit,
                        page = page,
                        email = fixtures.driver.email,
                    ),
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<GetUsersResponse>()

        // then
        assertThat(response.users).hasSize(1)
        assertThat(response.total).isEqualTo(1)
    }

    private fun generateUsers(
        limit: Int,
        jwt: JwtAccess,
    ): List<ResultActions> {
        val users = generateUsers(fixtures.driverInvitation, limit, jwt)
        val dispatchers = generateUsers(fixtures.invitation, limit, jwt)
        return users + dispatchers
    }

    private fun generateUsers(
        invitation: FixtureInvitation,
        limit: Int,
        jwt: JwtAccess,
    ) = List(limit) { invitation.toCreateInvitationRequest(randomEmail = true) }
        .map {
            routes.createAndAcceptInvitation(
                fleetId = fixtures.fleet.fleetId,
                inviteRequest = it,
                acceptRequest = AcceptInvitationRequest(UserPassword("password")),
                jwt = jwt,
            )
        }
}
