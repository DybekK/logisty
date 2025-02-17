package com.logisty.core.domain.service.order.reporter

import com.logisty.core.domain.model.Order
import com.logisty.core.domain.model.command.ReportOrderCommand
import com.logisty.core.domain.model.event.OrderCompletedEvent
import com.logisty.core.domain.model.event.OrderCompletedEvent.OrderCompletedPayload
import com.logisty.core.domain.model.event.OrderReportedEvent
import com.logisty.core.domain.model.event.OrderReportedEvent.OrderReportedPayload
import com.logisty.core.domain.model.event.OrderStartedEvent
import com.logisty.core.domain.model.event.OrderStartedEvent.OrderStartedPayload
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderStepId
import com.logisty.core.domain.port.EventStore
import com.logisty.core.domain.port.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional
class OrderReporter(
    private val clock: Clock,
    private val eventStore: EventStore,
    private val orderValidator: OrderValidator,
    private val orderRepository: OrderRepository,
) {
    fun reportOrder(command: ReportOrderCommand) =
        orderValidator
            .validateAndReturn(command.fleetId, command.orderId, command.stepId)
            .also { reportStep(command) }
            .also { startOrCompleteOrder(it, command) }
            .orderId

    private fun reportStep(command: ReportOrderCommand) {
        orderRepository.reportOrder(command)
        eventStore.append(command.toOrderReportedEvent(command.orderId, clock))
    }

    private fun startOrCompleteOrder(
        order: Order,
        command: ReportOrderCommand,
    ) {
        if (order.isFirstStep(command.stepId)) {
            orderRepository.startOrder(command.orderId)
            eventStore.append(command.toOrderStartedEvent(command.orderId, clock))
        }

        if (order.isLastStep(command.stepId)) {
            orderRepository.completeOrder(command.orderId)
            eventStore.append(command.toOrderCompletedEvent(command.orderId, clock))
        }
    }

    private fun Order.isFirstStep(stepId: OrderStepId): Boolean = steps.firstOrNull()?.orderStepId == stepId

    private fun Order.isLastStep(stepId: OrderStepId): Boolean = steps.lastOrNull()?.orderStepId == stepId
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
                actualArrivalAt = arrivedAt,
                location = location,
            ),
        appendedAt = clock.instant(),
    )

private fun ReportOrderCommand.toOrderStartedEvent(
    orderId: OrderId,
    clock: Clock,
): OrderStartedEvent =
    OrderStartedEvent(
        fleetId = fleetId,
        payload =
            OrderStartedPayload(
                orderId = orderId,
            ),
        appendedAt = clock.instant(),
    )

private fun ReportOrderCommand.toOrderCompletedEvent(
    orderId: OrderId,
    clock: Clock,
): OrderCompletedEvent =
    OrderCompletedEvent(
        fleetId = fleetId,
        payload = OrderCompletedPayload(orderId = orderId),
        appendedAt = clock.instant(),
    )
