package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.toInternalServerErrorResponseEntity
import com.logisty.core.domain.hub.OrderHub
import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.query.GetAvailableDriversQuery
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserId
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

// get available drivers
data class GetAvailableDriversResponse(
    val drivers: List<Driver>,
) {
    data class Driver(
        val driverId: UserId,
        val firstName: FirstName,
        val lastName: LastName,
    )
}

fun User.toGetDriverResponse() =
    GetAvailableDriversResponse.Driver(
        driverId = userId,
        firstName = firstName,
        lastName = lastName,
    )

@RestController
@RequestMapping("api/fleets")
class DriverController(
    private val orderHub: OrderHub,
) {
    private val logger = LoggerFactory.getLogger(DriverController::class.java)

    @GetMapping("/{fleetId}/drivers/available")
    fun getAvailableDrivers(
        @PathVariable fleetId: FleetId,
        @RequestParam startAt: Instant,
        @RequestParam endAt: Instant,
    ) = runCatching { orderHub.getAvailableDrivers(GetAvailableDriversQuery(fleetId, startAt, endAt)) }
        .map { drivers ->
            ResponseEntity.ok(
                GetAvailableDriversResponse(
                    drivers = drivers.map { it.toGetDriverResponse() },
                ),
            )
        }.getOrElse { it.toInternalServerErrorResponseEntity(logger) }
}
