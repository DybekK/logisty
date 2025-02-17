package com.logisty.core.adapter.outbound

import com.logisty.core.application.persistence.tables.OrderRoutes
import com.logisty.core.application.persistence.tables.OrderSteps
import com.logisty.core.application.persistence.tables.Orders
import com.logisty.core.domain.model.Order
import com.logisty.core.domain.model.OrderRoute
import com.logisty.core.domain.model.OrderStep
import com.logisty.core.domain.model.command.CreateOrderCommand
import com.logisty.core.domain.model.command.ReportOrderCommand
import com.logisty.core.domain.model.query.GetOrderQuery
import com.logisty.core.domain.model.query.GetOrdersQuery
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderRouteId
import com.logisty.core.domain.model.values.OrderStatus
import com.logisty.core.domain.model.values.OrderStatus.ASSIGNED
import com.logisty.core.domain.model.values.OrderStatus.CANCELLED
import com.logisty.core.domain.model.values.OrderStatus.COMPLETED
import com.logisty.core.domain.model.values.OrderStatus.PENDING
import com.logisty.core.domain.model.values.OrderStepId
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.port.OrderRepository
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.postgis.LineString
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class PostgresOrderRepository : OrderRepository {
    override fun findById(id: OrderId): Order? {
        val orderRow =
            Orders
                .innerJoin(OrderRoutes)
                .selectAll()
                .where { Orders.orderId eq id.value }
                .singleOrNull() ?: return null

        val steps =
            OrderSteps
                .selectAll()
                .where { OrderSteps.orderId eq id.value }
                .orderBy(OrderSteps.estimatedArrivalAt.isNull(), SortOrder.DESC)
                .orderBy(OrderSteps.estimatedArrivalAt, SortOrder.ASC)
                .map { it.toOrderStep() }

        val route =
            OrderRoutes
                .selectAll()
                .where { OrderRoutes.orderId eq id.value }
                .map { it.toOrderRoute() }
                .singleOrNull() ?: return null

        return orderRow.toOrder(steps, route)
    }

    override fun findUpcomingOrActiveOrder(query: GetOrderQuery): Order? {
        val orderRow =
            Orders
                .innerJoin(OrderRoutes)
                .selectAll()
                .where { Orders.fleetId eq query.fleetId.value }
                .andWhere { Orders.driverId eq query.driverId.value }
                .andWhere { Orders.status inList listOf(PENDING.name, ASSIGNED.name) }
                .andWhere {
                    (Orders.status eq PENDING.name) or
                        (
                            (Orders.estimatedStartedAt greaterEq (query.nearestTo - query.lookupRange)) and
                                (Orders.estimatedStartedAt lessEq (query.nearestTo + query.lookupRange))
                        )
                }.orderBy(Orders.estimatedStartedAt, SortOrder.ASC)
                .limit(1)
                .singleOrNull() ?: return null

        val steps =
            OrderSteps
                .selectAll()
                .where { OrderSteps.orderId eq orderRow[Orders.orderId] }
                .map { it.toOrderStep() }

        val route =
            OrderRoutes
                .selectAll()
                .where { OrderRoutes.orderId eq orderRow[Orders.orderId] }
                .map { it.toOrderRoute() }
                .singleOrNull() ?: return null

        return orderRow.toOrder(steps, route)
    }

    override fun findOrdersByStatus(status: OrderStatus): List<Order> {
        val orderRows =
            Orders
                .innerJoin(OrderRoutes)
                .selectAll()
                .where { Orders.status eq status.name }

        if (orderRows.empty()) return emptyList()

        val stepsByOrder =
            OrderSteps
                .selectAll()
                .where { OrderSteps.orderId inList orderRows.map { it[Orders.orderId] } }
                .groupBy { OrderId(it[OrderSteps.orderId]) }
                .mapValues { it.value.map { row -> row.toOrderStep() } }

        val routesMap =
            OrderRoutes
                .selectAll()
                .where { OrderRoutes.orderId inList orderRows.map { it[Orders.orderId] } }
                .orderBy(OrderSteps.estimatedArrivalAt.isNull(), SortOrder.DESC)
                .orderBy(OrderSteps.estimatedArrivalAt, SortOrder.ASC)
                .map { it.toOrderRoute() }
                .associateBy { it.orderId }

        return orderRows.map { row ->
            val orderId = OrderId(row[Orders.orderId])
            row.toOrder(stepsByOrder[orderId] ?: emptyList(), routesMap[orderId]!!)
        }
    }

    override fun findOrders(query: GetOrdersQuery): Pair<List<Order>, Long> {
        val count =
            Orders
                .selectAll()
                .where { Orders.fleetId eq query.fleetId.value }
                .andWhere { query.driverId?.let { Orders.driverId eq it.value } ?: Op.TRUE }
                .count()

        val orderRows =
            Orders
                .innerJoin(OrderRoutes)
                .selectAll()
                .where { Orders.fleetId eq query.fleetId.value }
                .andWhere { query.driverId?.let { Orders.driverId eq it.value } ?: Op.TRUE }
                .orderBy(Orders.createdAt, SortOrder.ASC)
                .limit(query.limit)
                .offset((query.page * query.limit).toLong())

        if (orderRows.empty()) return Pair(emptyList(), 0)

        val stepsByOrder =
            OrderSteps
                .selectAll()
                .where { OrderSteps.orderId inList orderRows.map { it[Orders.orderId] } }
                .orderBy(OrderSteps.estimatedArrivalAt.isNull(), SortOrder.DESC)
                .orderBy(OrderSteps.estimatedArrivalAt, SortOrder.ASC)
                .groupBy { OrderId(it[OrderSteps.orderId]) }
                .mapValues { it.value.map { row -> row.toOrderStep() } }

        val routesMap =
            OrderRoutes
                .selectAll()
                .where { OrderRoutes.orderId inList orderRows.map { it[Orders.orderId] } }
                .map { it.toOrderRoute() }
                .associateBy { it.orderId }

        val orders =
            orderRows.map { row ->
                val orderId = OrderId(row[Orders.orderId])
                row.toOrder(stepsByOrder[orderId] ?: emptyList(), routesMap[orderId]!!)
            }

        return Pair(orders, count)
    }

    override fun createOrder(
        command: CreateOrderCommand,
        createdAt: Instant,
    ) = insertOrder(command, createdAt)
        .also { orderId ->
            insertOrderSteps(orderId, command.steps)
            insertOrderRoute(orderId, command.route)
        }

    override fun updateRoute(
        fleetId: FleetId,
        orderId: OrderId,
        route: LineString,
    ): OrderId {
        OrderRoutes.update({ OrderRoutes.orderId eq orderId.value }) {
            it[OrderRoutes.routePoints] = route
        }

        return orderId
    }

    override fun reportOrder(command: ReportOrderCommand): OrderId {
        OrderSteps.update({
            (OrderSteps.orderStepId eq command.stepId.value) and (OrderSteps.orderId eq command.orderId.value)
        }) {
            it[actualArrivalAt] = command.arrivedAt
            it[location] = command.location
        }

        return command.orderId
    }

    override fun startOrder(orderId: OrderId): OrderId {
        Orders.update({ Orders.orderId eq orderId.value }) { it[status] = PENDING.name }
        return orderId
    }

    override fun completeOrder(orderId: OrderId): OrderId {
        Orders.update({ Orders.orderId eq orderId.value }) { it[status] = COMPLETED.name }
        return orderId
    }

    override fun cancelOrder(orderId: OrderId): OrderId {
        Orders.update({ Orders.orderId eq orderId.value }) {
            it[Orders.status] = CANCELLED.name
        }

        return orderId
    }

    private fun insertOrder(
        command: CreateOrderCommand,
        createdAt: Instant,
    ) = OrderId
        .generate()
        .also { orderId ->
            Orders.insert {
                it[Orders.orderId] = orderId.value
                it[Orders.fleetId] = command.fleetId.value
                it[Orders.driverId] = command.driverId.value
                it[Orders.status] = ASSIGNED.name
                it[Orders.estimatedStartedAt] = command.estimatedStartedAt
                it[Orders.estimatedEndedAt] = command.estimatedEndedAt
                it[Orders.createdBy] = command.createdBy.value
                it[Orders.createdAt] = createdAt
            }
        }

    private fun insertOrderSteps(
        orderId: OrderId,
        steps: List<CreateOrderCommand.OrderStep>,
    ) = steps.forEach { step ->
        OrderSteps.insert {
            it[OrderSteps.orderStepId] = OrderStepId.generate().value
            it[OrderSteps.orderId] = orderId.value
            it[OrderSteps.description] = step.description
            it[OrderSteps.location] = step.location
            it[OrderSteps.estimatedArrivalAt] = step.estimatedArrivalAt
        }
    }

    private fun insertOrderRoute(
        orderId: OrderId,
        route: CreateOrderCommand.OrderRoute,
    ) {
        OrderRoutes.insert {
            it[OrderRoutes.orderRouteId] = OrderRouteId.generate().value
            it[OrderRoutes.orderId] = orderId.value
            it[OrderRoutes.route] = route.route
            it[OrderRoutes.duration] = route.duration
            it[OrderRoutes.distance] = route.distance
        }
    }
}

private fun ResultRow.toOrderRoute(): OrderRoute =
    OrderRoute(
        orderRouteId = OrderRouteId(this[OrderRoutes.orderRouteId]),
        orderId = OrderId(this[OrderRoutes.orderId]),
        route = this[OrderRoutes.route],
        routePoints = this[OrderRoutes.routePoints],
        duration = this[OrderRoutes.duration],
        distance = this[OrderRoutes.distance],
    )

private fun ResultRow.toOrderStep(): OrderStep =
    OrderStep(
        orderStepId = OrderStepId(this[OrderSteps.orderStepId]),
        description = this[OrderSteps.description],
        location = this[OrderSteps.location],
        estimatedArrivalAt = this[OrderSteps.estimatedArrivalAt],
        actualArrivalAt = this[OrderSteps.actualArrivalAt],
    )

private fun ResultRow.toOrder(
    steps: List<OrderStep>,
    route: OrderRoute,
): Order =
    Order(
        orderId = OrderId(this[Orders.orderId]),
        fleetId = FleetId(this[Orders.fleetId]),
        driverId = UserId(this[Orders.driverId]),
        status = OrderStatus.valueOf(this[Orders.status]),
        steps = steps,
        route = route,
        createdBy = UserId(this[Orders.createdBy]),
        createdAt = this[Orders.createdAt],
        estimatedStartedAt = this[Orders.estimatedStartedAt],
        estimatedEndedAt = this[Orders.estimatedEndedAt],
    )
