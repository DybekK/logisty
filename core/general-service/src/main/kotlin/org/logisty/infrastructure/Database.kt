package org.logisty.infrastructure

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*

object Database {
    operator fun invoke(environment: ApplicationEnvironment): MongoDatabase {
        val host = environment.config.property("mongodb.host").getString()
        val port = environment.config.property("mongodb.port").getString()
        val user = environment.config.property("mongodb.user").getString()
        val password = environment.config.property("mongodb.password").getString()
        val database = environment.config.property("mongodb.database").getString()

        return MongoClient.create("mongodb://$user:$password@$host:$port").getDatabase(database)
    }
}