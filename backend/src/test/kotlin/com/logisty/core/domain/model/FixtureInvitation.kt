package com.logisty.core.domain.model

import com.logisty.core.adapter.inbound.CreateInvitationRequest
import com.logisty.core.domain.generateUserEmail
import com.logisty.core.domain.model.values.ApartmentNumber
import com.logisty.core.domain.model.values.City
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.PhoneNumber
import com.logisty.core.domain.model.values.PostalCode
import com.logisty.core.domain.model.values.StateProvince
import com.logisty.core.domain.model.values.Street
import com.logisty.core.domain.model.values.StreetNumber
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserRole
import java.time.Instant
import java.time.LocalDate

data class FixtureInvitation(
    val invitationId: InvitationId,
    val fleetId: FleetId,
    val fleetName: FleetName,
    val firstName: FirstName,
    val lastName: LastName,
    val email: UserEmail,
    val status: InvitationStatus,
    val phoneNumber: PhoneNumber,
    val dateOfBirth: LocalDate,
    val street: Street,
    val streetNumber: StreetNumber,
    val apartmentNumber: ApartmentNumber,
    val city: City,
    val stateProvince: StateProvince,
    val postalCode: PostalCode,
    val roles: List<UserRole>,
    val createdAt: Instant,
    val expiresAt: Instant,
    val acceptedAt: Instant?,
) {
    fun toCreateInvitationRequest(randomEmail: Boolean = true) =
        CreateInvitationRequest(
            email = if (randomEmail) generateUserEmail() else email,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            dateOfBirth = dateOfBirth,
            street = street,
            streetNumber = streetNumber,
            apartmentNumber = apartmentNumber,
            city = city,
            stateProvince = stateProvince,
            postalCode = postalCode,
            roles = roles,
        )
}
