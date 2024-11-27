package com.logisty.core.domain.service

import com.logisty.core.domain.BusinessExceptions.FleetAlreadyExistsException
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.port.FleetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FleetCreator(
    private val fleetRepository: FleetRepository,
) {
    fun createFleet(fleetName: FleetName): FleetId =
        fleetRepository
            .findByName(fleetName)
            ?.let { throw FleetAlreadyExistsException() }
            ?: fleetRepository.createFleet(fleetName)
}
