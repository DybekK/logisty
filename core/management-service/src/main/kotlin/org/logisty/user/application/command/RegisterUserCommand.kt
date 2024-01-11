package org.logisty.user.application.command

data class RegisterUserCommand(
    val name: String,
    val email: String,
    val password: String,
    val repeatPassword: String
)
