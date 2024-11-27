package com.logisty.core.application.security.jwt

import com.logisty.core.application.security.jwt.values.JwtRefresh
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class JwtStorage {
    private val tokens = ConcurrentHashMap<JwtRefresh, UserDetails>()

    fun findUserDetailsByToken(refreshJwt: JwtRefresh): UserDetails? =
        tokens[refreshJwt]

    fun save(jwt: JwtRefresh, user: UserDetails) {
        tokens.putIfAbsent(jwt, user)
    }

    fun remove(refreshJwt: JwtRefresh) {
        tokens.remove(refreshJwt)
    }
}
