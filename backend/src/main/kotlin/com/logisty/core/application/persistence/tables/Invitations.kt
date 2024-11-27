package com.logisty.core.application.persistence.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Invitations : Table() {
    val invitationId = uuid("invitation_id")
    val fleetId = uuid("fleet_id").references(Fleets.fleetId)
    val email = varchar("email", 255)
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val status = varchar("status", 255)
    val createdAt = timestamp("created_at")
    val expiresAt = timestamp("expires_at")
    val acceptedAt = timestamp("accepted_at").nullable()

    override val primaryKey = PrimaryKey(invitationId)
}
