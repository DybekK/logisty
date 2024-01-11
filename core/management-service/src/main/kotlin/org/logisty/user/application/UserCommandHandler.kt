package org.logisty.user.application

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.BadRequestException
import org.logisty.user.application.command.RegisterUserCommand
import org.logisty.user.application.result.UserCreated
import org.logisty.user.domain.User
import org.logisty.user.domain.UserService
import java.util.*

@ApplicationScoped
class UserCommandHandler(private val userService: UserService) {

    fun handle(command: RegisterUserCommand): Uni<UserCreated> {
        if (command.password != command.repeatPassword) {
            throw BadRequestException("Password and repeat password do not match")
        }

        val user = User(UUID.randomUUID(), command.name, command.email, command.password)
        return userService
            .createUser(user)
            .map { id -> UserCreated(id) }
    }
}