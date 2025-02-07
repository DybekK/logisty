package com.logisty.core.adapter.inbound.fleet

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.adapter.inbound.CreateInvitationRequest
import com.logisty.core.domain.ErrorCode
import com.logisty.core.domain.model.values.ApartmentNumber
import com.logisty.core.domain.model.values.City
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.PhoneNumber
import com.logisty.core.domain.model.values.PostalCode
import com.logisty.core.domain.model.values.StateProvince
import com.logisty.core.domain.model.values.Street
import com.logisty.core.domain.model.values.StreetNumber
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserRole
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

class CreateInvitationFunctionalTest : FunctionalTest() {
    @Test
    fun `should invite to fleet successfully`() {
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
            CreateInvitationRequest(
                email = fixtures.dispatcher.email,
                firstName = fixtures.dispatcher.firstName,
                lastName = fixtures.dispatcher.lastName,
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
