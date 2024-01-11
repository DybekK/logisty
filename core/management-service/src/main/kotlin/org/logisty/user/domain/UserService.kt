package org.logisty.user.domain

import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.logisty.user.infrastructure.UserRepository
import java.util.UUID

@ApplicationScoped
class UserService(private val userRepository: UserRepository) {

    @WithSession
    fun createUser(user: User): Uni<UUID> =
        userRepository
            .persistAndFlush(user)
            .map { _ -> user.id }
}