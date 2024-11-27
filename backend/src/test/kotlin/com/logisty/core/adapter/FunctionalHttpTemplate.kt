package com.logisty.core.adapter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.logisty.core.adapter.inbound.AcceptInvitationRequest
import com.logisty.core.adapter.inbound.AuthenticationRequest
import com.logisty.core.adapter.inbound.AuthenticationResponse
import com.logisty.core.adapter.inbound.CreateFleetRequest
import com.logisty.core.adapter.inbound.CreateInvitationRequest
import com.logisty.core.adapter.inbound.CreateInvitationResponse
import com.logisty.core.adapter.inbound.RefreshTokenRequest
import com.logisty.core.application.security.SecurityErrorCode
import com.logisty.core.application.security.jwt.values.JwtAccess
import com.logisty.core.application.security.jwt.values.JwtRefresh
import com.logisty.core.domain.ErrorCode
import com.logisty.core.domain.Fixtures
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserPassword
import org.hamcrest.Matchers.hasItem
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

class FunctionalHttpTemplate(
    private val mockMvc: MockMvc,
    private val fixtures: Fixtures
) {
    private val mapper = jacksonObjectMapper()

    // auth
    fun authenticateAndReturn(
        email: UserEmail = fixtures.user.email,
        password: UserPassword = fixtures.user.password
    ): Pair<JwtAccess, JwtRefresh> =
        authenticate(email, password)
            .andReturnResponse<AuthenticationResponse>()
            .let { response -> response.accessToken to response.refreshToken }

    fun authenticate(
        email: UserEmail,
        password: UserPassword,
    ): ResultActions =
        mockMvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(AuthenticationRequest(email, password))),
        )

    fun refresh(refreshToken: JwtRefresh): ResultActions =
        mockMvc.perform(
            post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(RefreshTokenRequest(refreshToken))),
        )

    // fleet
    fun createFleet(
        request: CreateFleetRequest,
        jwt: JwtAccess,
    ): ResultActions =
        mockMvc.perform(
            post("/api/fleets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwt.value}")
                .content(mapper.writeValueAsString(request)),
        )

    fun createInvitation(
        fleetId: FleetId,
        request: CreateInvitationRequest,
        jwt: JwtAccess,
    ): ResultActions =
        mockMvc.perform(
            post("/api/fleets/invite/${fleetId.value}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwt.value}")
                .content(mapper.writeValueAsString(request)),
        )

    fun acceptInvitation(
        invitationId: InvitationId,
        request: AcceptInvitationRequest,
        jwt: JwtAccess,
    ): ResultActions =
        mockMvc.perform(
            post("/api/fleets/accept/${invitationId.value}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwt.value}")
                .content(mapper.writeValueAsString(request)),
        )

    fun createAndAcceptInvitation(
        fleetId: FleetId,
        inviteRequest: CreateInvitationRequest,
        acceptRequest: AcceptInvitationRequest,
        jwt: JwtAccess,
    ): ResultActions =
        createInvitation(fleetId, inviteRequest, jwt)
            .andReturnResponse<CreateInvitationResponse>()
            .let { invitationResponse -> acceptInvitation(invitationResponse.invitationId, acceptRequest, jwt) }
}

inline fun <reified T> ResultActions.andReturnResponse(): T = readResponse<T>(andReturn().response)

inline fun <reified T> readResponse(response: MockHttpServletResponse): T =
    jacksonObjectMapper().readValue(response.contentAsString, T::class.java)

fun ResultActions.andExpectError(errorCode: ErrorCode) =
    andExpect(jsonPath("$.errors").isArray)
        .andExpect(jsonPath("$.errors").value(hasItem(errorCode.name)))

fun ResultActions.andExpectError(errorCode: SecurityErrorCode) =
    andExpect(jsonPath("$.errors").isArray)
        .andExpect(jsonPath("$.errors").value(hasItem(errorCode.name)))
