package org.logisty.infrastructure.es

import com.eventstore.dbclient.*
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import java.util.*

class EventStore(environment: ApplicationEnvironment) {
    private val client = connect(environment)
    private val eventSerializer = EventSerializer.serializer

    private fun connect(environment: ApplicationEnvironment): EventStoreDBClient {
        val host = environment.config.property("eventstoredb.host").getString()
        val port = environment.config.property("eventstoredb.port").getString()
        val settings = EventStoreDBConnectionString.parseOrThrow("esdb://$host:$port?tls=false")

        return EventStoreDBClient.create(settings)
    }

    suspend fun appendEvent(streamName: String, event: Event): WriteResult {
        val options = AppendToStreamOptions.get()
            .expectedRevision(ExpectedRevision.any())
        return appendEvent(streamName, event, options)
    }

    private suspend fun appendEvent(
        streamName: String,
        event: Event,
        options: AppendToStreamOptions
    ): WriteResult {
        val eventData = asJson(event)
        return withContext(Dispatchers.IO) {
            client.appendToStream(streamName, options, eventData).get()
        }
    }

    suspend fun subscribe(streamName: String, onEventReceived: suspend (Event) -> Unit): Subscription =
        subscribe(streamName, SubscribeToStreamOptions.get().fromEnd(), onEventReceived)

    private suspend fun subscribe(
        streamName: String,
        options: SubscribeToStreamOptions,
        onEventReceived: suspend (Event) -> Unit
    ): Subscription {
        val listener: SubscriptionListener = object : SubscriptionListener() {
            override fun onEvent(subscription: Subscription?, resolvedEvent: ResolvedEvent) {
                CoroutineScope(Dispatchers.IO).launch {
                    val eventData = resolvedEvent.originalEvent.eventData
                    val event = eventSerializer.decodeFromString<Event>(String(eventData))
                    onEventReceived(event)
                }
            }
        }

        return withContext(Dispatchers.IO) {
            client.subscribeToStream(streamName, listener, options).get()
        }
    }

    private fun asJson(event: Event): EventData {
        val json = eventSerializer.encodeToString(event)
        return EventData
            .builderAsBinary(UUID.randomUUID(), event.eventType(), json.toByteArray(Charsets.UTF_8))
            .build()
    }
}