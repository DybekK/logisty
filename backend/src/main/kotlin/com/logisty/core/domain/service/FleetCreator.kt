package com.logisty.core.domain.service

import com.logisty.core.domain.BusinessExceptions.FleetAlreadyExistsException
import com.logisty.core.domain.model.event.FleetCreatedEvent
import com.logisty.core.domain.model.event.FleetCreatedEvent.FleetCreatedPayload
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.port.EventStore
import com.logisty.core.domain.port.FleetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional
class FleetCreator(
    private val clock: Clock,
    private val eventStore: EventStore,
    private val fleetRepository: FleetRepository,
) {
    fun createFleet(fleetName: FleetName): FleetId =
        fleetRepository
            .findByName(fleetName)
            ?.let { throw FleetAlreadyExistsException() }
            ?: fleetRepository
                .createFleet(fleetName)
                .also { eventStore.append(fleetName.toFleetCreatedEvent(it, clock)) }
}

private fun FleetName.toFleetCreatedEvent(
    fleetId: FleetId,
    clock: Clock,
): FleetCreatedEvent =
    FleetCreatedEvent(
        fleetId = fleetId,
        payload = FleetCreatedPayload(fleetId = fleetId),
        appendedAt = clock.instant(),
    )
