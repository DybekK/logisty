package com.logisty.core.domain.service.order

import com.logisty.core.domain.BusinessExceptions.FleetNotFoundException
import com.logisty.core.domain.BusinessExceptions.OrderEstimatedStartTimeAfterEndTimeException
import com.logisty.core.domain.BusinessExceptions.StepEstimatedArrivalTimeInFutureException
import com.logisty.core.domain.BusinessExceptions.UserIsNotDispatcherException
import com.logisty.core.domain.BusinessExceptions.UserIsNotDriverException
import com.logisty.core.domain.model.command.CreateOrderCommand
import com.logisty.core.domain.model.event.OrderCreatedEvent
import com.logisty.core.domain.model.event.OrderCreatedEvent.OrderCreatedPayload
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserRole
import com.logisty.core.domain.port.EventStore
import com.logisty.core.domain.port.FleetRepository
import com.logisty.core.domain.port.OrderRepository
import com.logisty.core.domain.service.fleet.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
@Transactional
class OrderCreator(
    private val clock: Clock,
    private val eventStore: EventStore,
    private val fleetRepository: FleetRepository,
    private val orderRepository: OrderRepository,
    private val userService: UserService,
) {
    fun createOrder(command: CreateOrderCommand): OrderId {
        validateFleet(command.fleetId)
        validateDispatcher(command.createdBy)
        validateDriver(command.driverId)
        validateStepsTimings(command.steps)
        validateOrderTimings(command.estimatedStartedAt, command.estimatedEndedAt)

        return orderRepository
            .createOrder(command, clock.instant())
            .also { eventStore.append(command.toOrderCreatedEvent(it, clock)) }
    }

    private fun validateFleet(fleetId: FleetId) = fleetRepository.findById(fleetId) ?: throw FleetNotFoundException()

    private fun validateDispatcher(dispatcherId: UserId) {
        if (!userService.userHasRole(dispatcherId, UserRole.DISPATCHER)) {
            throw UserIsNotDispatcherException()
        }
    }

    private fun validateDriver(driverId: UserId) {
        if (!userService.userHasRole(driverId, UserRole.DRIVER)) {
            throw UserIsNotDriverException()
        }
    }

    private fun validateStepsTimings(steps: List<CreateOrderCommand.OrderStep>) {
        val now = clock.instant()

        if (steps.size > 1) {
            steps.dropLast(1).forEach { step ->
                if (step.estimatedArrivalAt?.isAfter(now) == true) {
                    throw StepEstimatedArrivalTimeInFutureException()
                }
            }
        }
    }

    private fun validateOrderTimings(
        estimatedStartedAt: Instant,
        estimatedEndedAt: Instant,
    ) {
        if (estimatedStartedAt.isAfter(estimatedEndedAt)) {
            throw OrderEstimatedStartTimeAfterEndTimeException()
        }
    }
}

private fun CreateOrderCommand.toOrderCreatedEvent(
    orderId: OrderId,
    clock: Clock,
) = OrderCreatedEvent(
    fleetId = fleetId,
    appendedAt = clock.instant(),
    payload =
        OrderCreatedPayload(
            orderId = orderId,
            driverId = driverId,
            steps = steps.map { it.toOrderStep() },
            estimatedStartedAt = estimatedStartedAt,
            estimatedEndedAt = estimatedEndedAt,
        ),
)

private fun CreateOrderCommand.OrderStep.toOrderStep() =
    OrderCreatedPayload.OrderStep(
        description = description,
        location = location,
        estimatedArrivalAt = estimatedArrivalAt,
        actualArrivalAt = actualArrivalAt,
    )
