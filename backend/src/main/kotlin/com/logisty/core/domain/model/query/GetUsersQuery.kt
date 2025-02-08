package com.logisty.core.domain.model.query

import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserRole

data class GetUsersQuery(
    val fleetId: FleetId,
    val page: Int,
    val limit: Int,
    val email: UserEmail? = null,
    val role: UserRole? = null,
)
