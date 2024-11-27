package com.logisty.core.application.security.jwt

import com.logisty.core.application.security.jwt.values.Jwt
import com.logisty.core.application.security.jwt.values.JwtProperties
import com.logisty.core.domain.model.values.UserEmail
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class JwtService(
    private val clock: Clock,
    jwtProperties: JwtProperties,
) {
    private val secretKey =
        Keys.hmacShaKeyFor(
            jwtProperties.key.toByteArray(),
        )

    fun generate(
        userDetails: UserDetails,
        expirationDate: Date,
    ): String =
        Jwts
            .builder()
            .claims()
            .subject(userDetails.username)
            .issuedAt(Date(clock.millis()))
            .expiration(expirationDate)
            .and()
            .signWith(secretKey)
            .compact()

    fun extractEmail(token: Jwt): UserEmail? =
        getAllClaims(token)
            .subject
            ?.let { UserEmail(it) }

    fun isValid(
        token: Jwt,
        userDetails: UserDetails,
    ): Boolean = userDetails.username == extractEmail(token)?.value && !isExpired(token)

    fun isExpired(token: Jwt): Boolean =
        getAllClaims(token)
            .expiration
            .before(Date(clock.millis()))

    private fun getAllClaims(token: Jwt): Claims {
        val parser =
            Jwts
                .parser()
                .verifyWith(secretKey)
                .build()

        return parser
            .parseSignedClaims(token.value)
            .payload
    }
}
