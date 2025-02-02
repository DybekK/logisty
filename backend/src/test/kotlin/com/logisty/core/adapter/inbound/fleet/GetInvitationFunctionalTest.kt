package com.logisty.core.adapter.inbound.fleet

import com.logisty.core.adapter.FunctionalTest
import com.logisty.core.adapter.andExpectError
import com.logisty.core.adapter.andExpectResponse
import com.logisty.core.domain.ErrorCode
import com.logisty.core.domain.model.values.InvitationId
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GetInvitationFunctionalTest : FunctionalTest() {
    @Test
    fun `should get invitation successfully`() {
        val (jwt, _) = routes.authenticateAndReturn()

        // when & then
        routes
            .getInvitation(fixtures.invitation.invitationId, jwt)
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 404 when invitation not found`() {
        val (jwt, _) = routes.authenticateAndReturn()

        routes
            .getInvitation(InvitationId.generate(), jwt)
            .andExpect(status().isNotFound)
            .andExpectError(ErrorCode.INVITATION_NOT_FOUND)
    }
}
