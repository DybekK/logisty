package com.logisty.core.application.persistence.tables

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userId = uuid("user_id")
    val fleetId = uuid("fleet_id").references(Fleets.fleetId)
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val email = varchar("email", 255)
    val password = varchar("password", 255)

    override val primaryKey = PrimaryKey(userId)
}
