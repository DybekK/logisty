package com.logisty.core.domain.model

import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName

data class FixtureFleet(
    val fleetId: FleetId,
    val fleetName: FleetName,
)
