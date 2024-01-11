package org.logisty.user.application

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.logisty.user.application.command.RegisterUserCommand
import org.logisty.user.application.result.UserCreated

@Path("/users")
@ApplicationScoped
class UserResource(private val userCommandHandler: UserCommandHandler) {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    fun registerUser(registerUserCommand: RegisterUserCommand): Uni<UserCreated> =
        userCommandHandler.handle(registerUserCommand)
}