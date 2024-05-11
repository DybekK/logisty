package org.logisty.module.order.infrastructure

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.toList
import org.logisty.module.order.domain.model.Order

class OrderRepository(mongo: MongoDatabase) {
    private val collection = mongo.getCollection<Order>("orders")

    suspend fun findPaginated(page: Int, size: Int): Pair<List<Order>, Long> {
        val count = collection.countDocuments()
        val orders = collection.find()
            .skip(page * size)
            .limit(size)
            .toList()

        return Pair(orders, count)
    }


    suspend fun save(order: Order) =
        collection.insertOne(order)
}