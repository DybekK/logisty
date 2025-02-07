package com.logisty.core.adapter.inbound.fleet

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.adapter.andReturnResponse
import com.logisty.core.adapter.inbound.AcceptInvitationRequest
import com.logisty.core.adapter.inbound.CreateInvitationRequest
import com.logisty.core.adapter.inbound.CreateInvitationResponse
import com.logisty.core.application.MutableClock
import com.logisty.core.domain.ErrorCode
import com.logisty.core.domain.model.values.ApartmentNumber
import com.logisty.core.domain.model.values.City
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.PhoneNumber
import com.logisty.core.domain.model.values.PostalCode
import com.logisty.core.domain.model.values.StateProvince
import com.logisty.core.domain.model.values.Street
import com.logisty.core.domain.model.values.StreetNumber
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.model.values.UserRole
import com.logisty.core.domain.service.fleet.InvitationService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration
import java.time.LocalDate

class AcceptInvitationFunctionalTest : FunctionalTest() {
    @Autowired
    private lateinit var invitationService: InvitationService

    @Test
    fun `should accept invitation successfully`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // given
        val inviteRequest =
            CreateInvitationRequest(
                email = UserEmail("test-user@example.com"),
                firstName = FirstName("John"),
                lastName = LastName("Doe"),
                phoneNumber = PhoneNumber("1234567890"),
                dateOfBirth = LocalDate.now().minusYears(18),
                street = Street("Main Street"),
                streetNumber = StreetNumber("123"),
                apartmentNumber = ApartmentNumber("A1"),
                city = City("New York"),
                stateProvince = StateProvince("NY"),
                postalCode = PostalCode("10001"),
                roles = listOf(UserRole.DRIVER),
            )
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
            CreateInvitationRequest(
                email = UserEmail("test-user@example.com"),
                firstName = FirstName("John"),
                lastName = LastName("Doe"),
                phoneNumber = PhoneNumber("1234567890"),
                dateOfBirth = LocalDate.now().minusYears(18),
                street = Street("Main Street"),
                streetNumber = StreetNumber("123"),
                apartmentNumber = ApartmentNumber("A1"),
                city = City("New York"),
                stateProvince = StateProvince("NY"),
                postalCode = PostalCode("10001"),
                roles = listOf(UserRole.DRIVER),
            )

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
        invitationService.expireInvitations()

        routes
            .acceptInvitation(
                invitationResponse.invitationId,
                acceptRequest,
            ).andExpect(status().isBadRequest)
            .andExpectError(ErrorCode.INVITATION_EXPIRED)
    }
}
