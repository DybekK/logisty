package org.logisty.user.infrastructure

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

import org.logisty.user.domain.User
import java.util.UUID

@ApplicationScoped
class UserRepository : PanacheRepositoryBase<User, UUID>
