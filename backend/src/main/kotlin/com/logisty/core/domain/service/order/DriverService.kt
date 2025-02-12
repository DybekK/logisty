package com.logisty.core.domain.service.order

import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.query.GetAvailableDriversQuery
import com.logisty.core.domain.port.DriverRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DriverService(
    private val driverRepository: DriverRepository,
) {
    fun getAvailableDrivers(query: GetAvailableDriversQuery): List<User> = driverRepository.findAvailableDrivers(query)
}
