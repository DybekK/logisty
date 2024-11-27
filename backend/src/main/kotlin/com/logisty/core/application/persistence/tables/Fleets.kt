package com.logisty.core.application.persistence.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Fleets : Table() {
    val fleetId = uuid("fleet_id")
    val fleetName = varchar("fleet_name", 255)

    override val primaryKey = PrimaryKey(fleetId)
}
