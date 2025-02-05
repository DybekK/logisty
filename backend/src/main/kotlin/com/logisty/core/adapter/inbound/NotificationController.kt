package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.outbound.TransactionalEventStore
import com.logisty.core.adapter.toInternalServerErrorResponseEntity
import com.logisty.core.domain.model.event.InternalEvent
import com.logisty.core.domain.model.values.FleetId
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

data class GetNotificationsResponse(
    val notifications: List<InternalEvent>,
)

@RestController
@RequestMapping("api/fleets")
class NotificationController(
    private val eventStore: TransactionalEventStore,
) {
    private val logger = LoggerFactory.getLogger(NotificationController::class.java)

    @GetMapping("/{fleetId}/notifications")
    fun getNotifications(
        @PathVariable fleetId: FleetId,
        @RequestParam since: Instant,
    ) = runCatching { eventStore.findSince(fleetId, since) }
        .map { ResponseEntity.ok(GetNotificationsResponse(it)) }
        .getOrElse { it.toInternalServerErrorResponseEntity(logger) }
}
