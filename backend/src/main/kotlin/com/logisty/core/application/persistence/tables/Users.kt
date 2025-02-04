package com.logisty.core.application.persistence.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object Users : Table() {
    val userId = uuid("user_id")
    val fleetId = uuid("fleet_id").references(Fleets.fleetId)
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val email = varchar("email", 255)
    val password = varchar("password", 255)
    val roles = array<String>("roles")
    val phoneNumber = varchar("phone_number", 255)
    val dateOfBirth = date("date_of_birth")
    val street = varchar("street", 255)
    val streetNumber = varchar("street_number", 255)
    val apartmentNumber = varchar("apartment_number", 255).nullable()
    val city = varchar("city", 255)
    val stateProvince = varchar("state_province", 255)
    val postalCode = varchar("postal_code", 255)

    override val primaryKey = PrimaryKey(userId)
}
