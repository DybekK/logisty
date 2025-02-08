package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.toInternalServerErrorResponseEntity
import com.logisty.core.domain.hub.FleetHub
import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.query.GetUsersQuery
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserRole
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

data class GetUserResponse(
    val userId: UserId,
    val firstName: FirstName,
    val lastName: LastName,
    val email: UserEmail,
    val roles: List<UserRole>,
    val createdAt: Instant,
)

fun User.toGetUserResponse() =
    GetUserResponse(
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        email = email,
        roles = roles,
        createdAt = createdAt,
    )

data class GetUsersResponse(
    val users: List<GetUserResponse>,
    val total: Long,
)

@RestController
@RequestMapping("api/fleets")
class UserController(
    private val fleetHub: FleetHub,
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/{fleetId}/users")
    fun getUsers(
        @PathVariable fleetId: FleetId,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam email: UserEmail?,
        @RequestParam role: UserRole?,
    ) = runCatching { fleetHub.getUsers(GetUsersQuery(fleetId, page, limit, email, role)) }
        .map { (users, total) ->
            ResponseEntity.ok(
                GetUsersResponse(
                    users = users.map { it.toGetUserResponse() },
                    total = total,
                ),
            )
        }.getOrElse { it.toInternalServerErrorResponseEntity(logger) }
}
