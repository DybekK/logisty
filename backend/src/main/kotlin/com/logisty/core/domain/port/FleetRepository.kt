package com.logisty.core.domain.port

import com.logisty.core.domain.model.Fleet
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName

interface FleetRepository {
    fun findById(fleetId: FleetId): Fleet?

    fun findByName(fleetName: FleetName): Fleet?

    fun createFleet(fleetName: FleetName): FleetId
}
