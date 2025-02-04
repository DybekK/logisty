package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.toBadRequestResponseEntity
import com.logisty.core.adapter.toInternalServerErrorResponseEntity
import com.logisty.core.domain.BusinessExceptions.FleetAlreadyExistsException
import com.logisty.core.domain.hub.FleetHub
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// create fleet
data class CreateFleetRequest(
    val fleetName: FleetName,
)

data class CreateFleetResponse(
    val fleetId: FleetId,
)

@RestController
@RequestMapping("api/fleets")
class FleetController(
    private val fleetHub: FleetHub,
) {
    private val logger = LoggerFactory.getLogger(FleetController::class.java)

    @PostMapping("/create")
    fun createFleet(
        @RequestBody request: CreateFleetRequest,
    ) = runCatching { fleetHub.createFleet(request.fleetName) }
        .map { ResponseEntity.ok(CreateFleetResponse(it)) }
        .getOrElse {
            when (it) {
                is FleetAlreadyExistsException -> it.toBadRequestResponseEntity()
                else -> it.toInternalServerErrorResponseEntity(logger)
            }
        }
}
