package com.logisty.core.domain.service.order.reporter

import com.logisty.core.domain.BusinessExceptions.FleetNotFoundException
import com.logisty.core.domain.BusinessExceptions.OrderNotFoundException
import com.logisty.core.domain.BusinessExceptions.OrderStepInvalidSequenceException
import com.logisty.core.domain.BusinessExceptions.OrderStepNotFoundException
import com.logisty.core.domain.model.Order
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderStepId
import com.logisty.core.domain.port.FleetRepository
import com.logisty.core.domain.port.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderValidator(
    private val orderRepository: OrderRepository,
    private val fleetRepository: FleetRepository,
) {
    fun validateAndReturn(
        fleetId: FleetId,
        orderId: OrderId,
        stepId: OrderStepId,
    ): Order =
        validateFleet(fleetId)
            .let { validateOrder(orderId, stepId) }

    private fun validateFleet(fleetId: FleetId) {
        fleetRepository.findById(fleetId)
            ?: throw FleetNotFoundException()
    }

    private fun validateOrder(
        orderId: OrderId,
        stepId: OrderStepId,
    ) = (orderRepository.findById(orderId) ?: throw OrderNotFoundException())
        .also { it.validateSteps(stepId) }

    private fun Order.validateSteps(stepId: OrderStepId) {
        val currentStepIndex =
            steps
                .indexOfFirst { it.orderStepId == stepId }
                .takeIf { it >= 0 } ?: throw OrderStepNotFoundException()

        val reportedStepIndices =
            steps
                .mapIndexed { index, step -> index to step }
                .filter { it.second.actualArrivalAt != null }
                .map { it.first }

        val expectedReportedIndices = (0 until currentStepIndex).toList()
        if (reportedStepIndices != expectedReportedIndices) {
            throw OrderStepInvalidSequenceException()
        }
    }
}
