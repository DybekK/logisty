package com.logisty.core.domain.service.order

import com.logisty.core.domain.BusinessExceptions.CannotTrackDriverLocationException
import com.logisty.core.domain.BusinessExceptions.FleetNotFoundException
import com.logisty.core.domain.BusinessExceptions.OrderNotFoundException
import com.logisty.core.domain.model.Order
import com.logisty.core.domain.model.command.TrackDriverLocationCommand
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderStatus
import com.logisty.core.domain.port.FleetRepository
import com.logisty.core.domain.port.OrderRepository
import org.postgis.LineString
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DriverLocationTracker(
    private val fleetRepository: FleetRepository,
    private val orderRepository: OrderRepository,
) {
    fun trackDriverLocation(command: TrackDriverLocationCommand): OrderId =
        validateFleet(command.fleetId)
            .let { validateAndReturnOrder(command.orderId) }
            .let { appendRoute(it, command.route) }

    private fun appendRoute(
        order: Order,
        route: LineString,
    ) = LineString(order.route.route.points + route.points)
        .let { updatedRoute ->
            orderRepository.updateRoute(order.fleetId, order.orderId, updatedRoute)
        }

    private fun validateFleet(fleetId: FleetId) {
        fleetRepository.findById(fleetId) ?: throw FleetNotFoundException()
    }

    private fun validateAndReturnOrder(orderId: OrderId): Order {
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException()

        if (order.status != OrderStatus.PENDING) {
            throw CannotTrackDriverLocationException()
        }

        return order
    }
}
