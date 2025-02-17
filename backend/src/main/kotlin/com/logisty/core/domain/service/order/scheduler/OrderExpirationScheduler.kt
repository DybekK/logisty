package com.logisty.core.domain.service.order.scheduler

import com.logisty.core.domain.service.order.OrderService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OrderExpirationScheduler(
    private val orderService: OrderService,
) {
    private val logger = LoggerFactory.getLogger(OrderExpirationScheduler::class.java)

    @Scheduled(fixedRate = 60000)
    fun scheduleExpireOrders() =
        orderService
            .cancelOrders()
            .takeIf { it.isNotEmpty() }
            ?.also { logger.info("Cancelled {} orders: {}", it.size, it.map { it.value }) }
}
