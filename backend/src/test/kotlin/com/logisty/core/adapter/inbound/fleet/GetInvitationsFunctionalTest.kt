package com.logisty.core.adapter.inbound.fleet

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andReturnResponse
import com.logisty.core.adapter.inbound.GetInvitationsResponse
import com.logisty.core.application.security.jwt.values.JwtAccess
import com.logisty.core.domain.model.query.GetInvitationsQuery
import com.logisty.core.domain.model.values.InvitationStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GetInvitationsFunctionalTest : FunctionalTest() {
    @Test
    fun `should get invitations`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val limit = 10
        val page = 0
        val invitations = generateInvitations(limit + 10, jwt)
        val invitationsSize = invitations.size + 2

        // when
        val response =
            routes
                .getInvitations(GetInvitationsQuery(fixtures.fleet.fleetId, page, limit), jwt)
                .andExpect(status().isOk)
                .andReturnResponse<GetInvitationsResponse>()

        // then
        assertThat(response.invitations).hasSize(limit)
        assertThat(response.total).isEqualTo(invitationsSize.toLong())
    }

    @Test
    fun `should get invitations with pagination`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val limit = 10
        val page = 2
        val invitations = generateInvitations(limit + 10, jwt)
        val invitationsSize = invitations.size + 2

        // when
        val response =
            routes
                .getInvitations(GetInvitationsQuery(fixtures.fleet.fleetId, page, limit), jwt)
                .andExpect(status().isOk)
                .andReturnResponse<GetInvitationsResponse>()

        // then
        val expectedSize = invitationsSize - limit * page
        assertThat(response.invitations).hasSize(expectedSize)
        assertThat(response.total).isEqualTo(invitationsSize.toLong())
    }

    @Test
    fun `should get invitations with status`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val limit = 10
        val page = 0
        generateInvitations(limit + 10, jwt)

        // when
        val response =
            routes
                .getInvitations(
                    GetInvitationsQuery(
                        fleetId = fixtures.fleet.fleetId,
                        page = page,
                        limit = limit,
                        status = InvitationStatus.ACCEPTED,
                    ),
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<GetInvitationsResponse>()

        // then
        assertThat(response.invitations).hasSize(2)
        assertThat(response.total).isEqualTo(2)
    }

    @Test
    fun `should get invitations with email`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val limit = 10
        val page = 0
        generateInvitations(limit + 10, jwt)

        // when
        val response =
            routes
                .getInvitations(
                    GetInvitationsQuery(
                        fleetId = fixtures.fleet.fleetId,
                        page = page,
                        limit = limit,
                        email = fixtures.invitation.email,
                    ),
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<GetInvitationsResponse>()

        // then
        assertThat(response.invitations).hasSize(1)
        assertThat(response.total).isEqualTo(1)
    }

    private fun generateInvitations(
        limit: Int,
        jwt: JwtAccess,
    ) = List(limit) { fixtures.invitation.toCreateInvitationRequest() }
        .map { routes.createInvitation(fixtures.fleet.fleetId, it, jwt) }
}
