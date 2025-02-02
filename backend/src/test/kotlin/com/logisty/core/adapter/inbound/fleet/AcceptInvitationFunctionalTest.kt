package com.logisty.core.adapter.inbound.fleet

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.adapter.andReturnResponse
import com.logisty.core.adapter.inbound.AcceptInvitationRequest
import com.logisty.core.adapter.inbound.CreateInvitationRequest
import com.logisty.core.adapter.inbound.CreateInvitationResponse
import com.logisty.core.application.MutableClock
import com.logisty.core.domain.ErrorCode
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.model.values.UserRole
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration

class AcceptInvitationFunctionalTest : FunctionalTest() {
    @Test
    fun `should accept invitation successfully`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val inviteRequest =
            CreateInvitationRequest(UserEmail("test-user@example.com"), FirstName("John"), LastName("Doe"), listOf(UserRole.DRIVER))
        val acceptRequest =
            AcceptInvitationRequest(UserPassword("password"))

        // when & then
        routes
            .createAndAcceptInvitation(
                fixtures.fleet.fleetId,
                inviteRequest,
                acceptRequest,
                jwt,
            ).andExpect(status().isOk)
    }

    @Test
    fun `should return 400 when invitation does not exist`() {
        // given
        val nonExistentInvitationId = InvitationId.generate()

        // when & then
        val acceptRequest = AcceptInvitationRequest(UserPassword("password"))
        routes
            .acceptInvitation(
                nonExistentInvitationId,
                acceptRequest,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.INVITATION_NOT_FOUND)
    }

    @Test
    fun `should return 400 when invitation is already accepted`() {
        // given
        val acceptRequest = AcceptInvitationRequest(UserPassword("password"))

        // when & then
        routes
            .acceptInvitation(
                fixtures.invitation.invitationId,
                acceptRequest,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.INVITATION_ALREADY_ACCEPTED)
    }

    @Test
    fun `should return 400 when invitation is expired`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val invitationRequest =
            CreateInvitationRequest(UserEmail("test-user@example.com"), FirstName("John"), LastName("Doe"), listOf(UserRole.DRIVER))

        // when
        val invitationResponse =
            routes
                .createInvitation(
                    fixtures.fleet.fleetId,
                    invitationRequest,
                    jwt,
                ).andExpect(status().isOk)
                .andReturnResponse<CreateInvitationResponse>()

        val acceptRequest = AcceptInvitationRequest(UserPassword("password"))

        // when & then
        (clock as MutableClock).advanceBy(Duration.ofDays(8))

        routes
            .acceptInvitation(
                invitationResponse.invitationId,
                acceptRequest,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.INVITATION_EXPIRED)
    }
}
