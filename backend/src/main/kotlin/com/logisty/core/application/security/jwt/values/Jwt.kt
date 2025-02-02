package com.logisty.core.application.security.jwt.values

sealed interface Jwt {
    val value: String
}

@JvmInline
value class JwtAccess(
    override val value: String,
) : Jwt

@JvmInline
value class JwtRefresh(
    override val value: String,
) : Jwt

fun String?.doesNotContainBearerToken() = this == null || !this.startsWith("Bearer ")

fun String.extractTokenValue() = JwtAccess(this.substringAfter("Bearer "))