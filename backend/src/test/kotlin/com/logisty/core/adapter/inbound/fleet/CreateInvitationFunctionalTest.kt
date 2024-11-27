package com.logisty.core.adapter.inbound.fleet

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.adapter.inbound.CreateInvitationRequest
import com.logisty.core.domain.ErrorCode
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CreateInvitationFunctionalTest : FunctionalTest() {
    @Test
    fun `should invite to fleet successfully`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val inviteRequest =
            CreateInvitationRequest(UserEmail("test-user@example.com"), FirstName("John"), LastName("Doe"))

        // when & then
        routes
            .createInvitation(
                fixtures.fleet.fleetId,
                inviteRequest,
                jwt,
            ).andExpect(status().isOk)
    }

    @Test
    fun `should return 400 when invitation already exists`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val inviteRequest =
            CreateInvitationRequest(UserEmail("test-user@example.com"), FirstName("John"), LastName("Doe"))

        routes
            .createInvitation(
                fixtures.fleet.fleetId,
                inviteRequest,
                jwt,
            ).andExpect(status().isOk)

        // when & then
        routes
            .createInvitation(
                fixtures.fleet.fleetId,
                inviteRequest,
                jwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.INVITATION_ALREADY_EXISTS)
    }

    @Test
    fun `should return 400 when user already exists`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val inviteRequest =
            CreateInvitationRequest(fixtures.user.email, fixtures.user.firstName, fixtures.user.lastName)

        // when & then
        routes
            .createInvitation(
                fixtures.fleet.fleetId,
                inviteRequest,
                jwt,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.USER_ALREADY_EXISTS)
    }
}
