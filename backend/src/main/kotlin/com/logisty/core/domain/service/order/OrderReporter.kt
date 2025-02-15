package com.logisty.core.domain.service.order

import com.logisty.core.domain.BusinessExceptions.FleetNotFoundException
import com.logisty.core.domain.BusinessExceptions.OrderNotFoundException
import com.logisty.core.domain.BusinessExceptions.OrderStepNotFoundException
import com.logisty.core.domain.model.command.ReportOrderCommand
import com.logisty.core.domain.model.event.OrderReportedEvent
import com.logisty.core.domain.model.event.OrderReportedEvent.OrderReportedPayload
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderStepId
import com.logisty.core.domain.port.EventStore
import com.logisty.core.domain.port.FleetRepository
import com.logisty.core.domain.port.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional
class OrderReporter(
    private val clock: Clock,
    private val eventStore: EventStore,
    private val orderRepository: OrderRepository,
    private val fleetRepository: FleetRepository,
) {
    fun reportOrder(command: ReportOrderCommand): OrderId {
        validateFleet(command.fleetId)
        validateOrder(command.orderId, command.stepId)

        return orderRepository
            .reportOrder(command)
            .also { eventStore.append(command.toOrderReportedEvent(it, clock)) }
    }

    private fun validateFleet(fleetId: FleetId) {
        fleetRepository.findById(fleetId)
            ?: throw FleetNotFoundException()
    }

    private fun validateOrder(
        orderId: OrderId,
        stepId: OrderStepId,
    ) {
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException()

        if (!order.steps.any { it.orderStepId == stepId }) {
            throw OrderStepNotFoundException()
        }
    }
}

private fun ReportOrderCommand.toOrderReportedEvent(
    orderId: OrderId,
    clock: Clock,
): OrderReportedEvent =
    OrderReportedEvent(
        fleetId = fleetId,
        payload =
            OrderReportedPayload(
                orderId = orderId,
                stepId = stepId,
                actualArrivalAt = actualArrivalAt,
                location = location,
            ),
        appendedAt = clock.instant(),
    )
