package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andReturnResponse
import com.logisty.core.application.MutableClock
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration
import java.util.Locale
import kotlin.test.assertEquals

class GetNotificationsFunctionalTest : FunctionalTest() {
    @Test
    fun `should get notifications`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // when
        routes
            .createInvitation(
                fleetId = fixtures.fleet.fleetId,
                request = fixtures.invitation.toCreateInvitationRequest(),
                jwt = jwt,
            ).andExpect(status().isOk)

        // when & then
        val since = clock.instant().minus(Duration.ofDays(1))
        routes
            .getNotifications(fixtures.fleet.fleetId, since, jwt)
            .andExpect(status().isOk)
            .andReturnResponse<GetNotificationsResponse>()
            .let { assertEquals(1, it.notifications.size) }
    }

    @Test
    fun `should get notifications for driver`() {
        val (dispatcherJwt, _) = routes.authenticateAndReturn(fixtures.dispatcher.email, fixtures.dispatcher.password)
        val (driverJwt, _) = routes.authenticateAndReturn(fixtures.driver.email, fixtures.driver.password)

        // when
        routes
            .createInvitation(
                fleetId = fixtures.fleet.fleetId,
                request = fixtures.invitation.toCreateInvitationRequest(),
                jwt = dispatcherJwt,
            ).andExpect(status().isOk)

        // when & then
        val since = clock.instant().minus(Duration.ofDays(1))
        routes
            .getNotifications(fixtures.fleet.fleetId, since, driverJwt)
            .andExpect(status().isOk)
            .andReturnResponse<GetNotificationsResponse>()
            .let { assertEquals(0, it.notifications.size) }
    }

    @Test
    fun `should get notifications since given time`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // when
        routes
            .createInvitation(
                fleetId = fixtures.fleet.fleetId,
                request = fixtures.invitation.toCreateInvitationRequest(),
                jwt = jwt,
            ).andExpect(status().isOk)
        (clock as MutableClock).advanceBy(Duration.ofHours(1))

        // when
        routes
            .createInvitation(
                fleetId = fixtures.fleet.fleetId,
                request = fixtures.invitation.toCreateInvitationRequest(),
                jwt = jwt,
            ).andExpect(status().isOk)

        // when & then
        val since = clock.instant().minus(Duration.ofHours(1))
        routes
            .getNotifications(fixtures.fleet.fleetId, since, jwt)
            .andExpect(status().isOk)
            .andReturnResponse<GetNotificationsResponse>()
            .let { assertEquals(1, it.notifications.size) }
    }

    @Test
    fun `should get notifications translated`() {
        val (jwt, _) = routes.authenticateAndReturn()
        val locale = Locale.forLanguageTag("pl")

        // when
        routes
            .createInvitation(
                fleetId = fixtures.fleet.fleetId,
                request = fixtures.invitation.toCreateInvitationRequest(),
                jwt = jwt,
            ).andExpect(status().isOk)

        // when & then
        val since = clock.instant().minus(Duration.ofDays(1))
        routes
            .getNotifications(fixtures.fleet.fleetId, since, jwt, locale)
            .andExpect(status().isOk)
            .andReturnResponse<GetNotificationsResponse>()
            .let {
                assertEquals(1, it.notifications.size)
                assertEquals("Zaproszenie zostało wysłane", it.notifications[0].title.value)
            }
    }
}
