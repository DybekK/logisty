package com.logisty.core.domain.model

import com.logisty.core.domain.model.values.ApartmentNumber
import com.logisty.core.domain.model.values.City
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.PhoneNumber
import com.logisty.core.domain.model.values.PostalCode
import com.logisty.core.domain.model.values.StateProvince
import com.logisty.core.domain.model.values.Street
import com.logisty.core.domain.model.values.StreetNumber
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.model.values.UserRole
import java.time.LocalDate

data class FixtureUser(
    val userId: UserId,
    val fleetId: FleetId,
    val firstName: FirstName,
    val lastName: LastName,
    val email: UserEmail,
    val phoneNumber: PhoneNumber,
    val dateOfBirth: LocalDate,
    val street: Street,
    val streetNumber: StreetNumber,
    val apartmentNumber: ApartmentNumber,
    val city: City,
    val stateProvince: StateProvince,
    val postalCode: PostalCode,
    val password: UserPassword,
    val roles: List<UserRole>,
)
