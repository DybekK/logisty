package org.logisty.infrastructure

import com.eventstore.dbclient.AppendToStreamOptions
import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString
import com.eventstore.dbclient.WriteResult
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.logisty.application.event.OrderCreated
import java.util.*

interface Event {
    fun eventType(): String
}

class EventStore(environment: ApplicationEnvironment) {
    private val client = connect(environment)

    private fun connect(environment: ApplicationEnvironment): EventStoreDBClient {
        val host = environment.config.property("eventstoredb.host").getString()
        val port = environment.config.property("eventstoredb.port").getString()
        val settings = EventStoreDBConnectionString.parseOrThrow("esdb://$host:$port?tls=false")

        return EventStoreDBClient.create(settings)
    }

    suspend fun appendEvent(streamName: String, event: OrderCreated, options: AppendToStreamOptions): WriteResult {
        val eventData = asJson(event)

        return withContext(Dispatchers.IO) {
            client.appendToStream(streamName, options, eventData).get()
        }
    }

    private fun asJson(event: Event): EventData {
        val eventId = UUID.randomUUID()
        val eventType = event.eventType()

        return EventData.builderAsJson(eventId, eventType, event).build()
    }
}