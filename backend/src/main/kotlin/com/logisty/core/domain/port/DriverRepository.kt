package com.logisty.core.domain.port

import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.query.GetAvailableDriversQuery

interface DriverRepository {
    fun findAvailableDrivers(query: GetAvailableDriversQuery): List<User>
}
