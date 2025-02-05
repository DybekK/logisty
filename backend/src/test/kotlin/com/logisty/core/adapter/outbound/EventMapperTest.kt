package com.logisty.core.adapter.outbound

import com.logisty.core.application.mapper
import com.logisty.core.domain.model.event.InternalEvent
import com.logisty.core.domain.model.event.InvitationCreatedEvent
import com.logisty.core.domain.model.event.InvitationCreatedEvent.InvitationCreatedPayload
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals

class EventMapperTest {
    @Test
    fun `should properly serialize and deserialize event`() {
        val event: InternalEvent = invitationEvent()

        val serialized = mapper.writeValueAsString(event)
        val deserialized = mapper.readValue(serialized, InternalEvent::class.java)

        assertEquals(event, deserialized)
    }

    private fun invitationEvent(): InternalEvent =
        InvitationCreatedEvent(
            fleetId = FleetId.generate(),
            appendedAt = Instant.now(),
            payload =
                InvitationCreatedPayload(
                    invitationId = InvitationId.generate(),
                    email = UserEmail("test@test.com"),
                    firstName = FirstName("John"),
                    lastName = LastName("Doe"),
                ),
        )
}
