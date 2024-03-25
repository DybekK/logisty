package org.logisty.module.order.infrastructure

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.logisty.module.order.domain.model.Order

class OrderRepository(mongo: MongoDatabase) {
    private val collection = mongo.getCollection<Order>("orders")

    suspend fun save(order: Order) = collection.insertOne(order)
}