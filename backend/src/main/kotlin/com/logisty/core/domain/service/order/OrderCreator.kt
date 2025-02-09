package com.logisty.core.domain.service.order

import com.logisty.core.domain.BusinessExceptions
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
class OrderCreator(
    private val clock: Clock,
    private val eventStore: EventStore,
    private val orderRepository: OrderRepository,
) {
    fun createOrder() = TODO()
}
