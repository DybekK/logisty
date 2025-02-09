package com.logisty.core.application.persistence.tables

import com.c0x12c.exposed.postgis.lineString
import com.c0x12c.exposed.postgis.point
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object OrderSteps : Table() {
    val orderStepId = uuid("order_step_id")
    val orderId = uuid("order_id").references(Orders.orderId)

    val input = text("input")
    val location = point("location")

    val estimatedStartedAt = timestamp("estimated_started_at")
    val estimatedEndedAt = timestamp("estimated_ended_at")

    override val primaryKey = PrimaryKey(orderStepId)
}

object OrderRoutes : Table() {
    val orderRouteId = uuid("order_route_id")
    val orderId = uuid("order_id").references(Orders.orderId)

    val route = lineString("geometry")
    val duration = double("duration")
    val distance = double("distance")

    override val primaryKey = PrimaryKey(orderRouteId)
}

object Orders : Table() {
    val orderId = uuid("order_id")
    val fleetId = uuid("fleet_id").references(Fleets.fleetId)
    val driverId = uuid("driver_id").references(Users.userId)

    val estimatedStartedAt = timestamp("estimated_started_at")
    val estimatedEndedAt = timestamp("estimated_ended_at")

    val createdBy = uuid("created_by").references(Users.userId)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(orderId)
}
