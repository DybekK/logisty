package com.logisty.core.application.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.logisty.core.adapter.toUnauthorizedResponseEntity
import com.logisty.core.application.security.CustomUserDetailsService
import com.logisty.core.application.security.SecurityException
import com.logisty.core.application.security.SecurityExceptions.InvalidTokenStructureException
import com.logisty.core.application.security.SecurityExceptions.TokenExpiredOrNotFoundException
import com.logisty.core.application.security.jwt.values.doesNotContainBearerToken
import com.logisty.core.application.security.jwt.values.extractTokenValue
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val mapper: ObjectMapper,
    private val userDetailsService: CustomUserDetailsService,
    private val jwtService: JwtService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authHeader!!.extractTokenValue()
        val email = jwtService.extractEmail(jwtToken)

        if (email == null) {
            response.handleInvalidTokenStructureException()
            return
        }

        val foundUser =
            try {
                userDetailsService.loadUserByUsername(email.value)
            } catch (e: UsernameNotFoundException) {
                response.handleTokenExpiredOrNotFoundException()
                return
            }

        if (!jwtService.isValid(jwtToken, foundUser)) {
            response.handleTokenExpiredOrNotFoundException()
            return
        }

        updateContext(foundUser, request)
        filterChain.doFilter(request, response)
    }

    private fun HttpServletResponse.returnException(exception: SecurityException) =
        exception.toUnauthorizedResponseEntity().let {
            status = it.statusCode.value()
            contentType = "application/json"
            writer.write(mapper.writeValueAsString(it.body))
        }

    private fun HttpServletResponse.handleTokenExpiredOrNotFoundException() = returnException(TokenExpiredOrNotFoundException())

    private fun HttpServletResponse.handleInvalidTokenStructureException() = returnException(InvalidTokenStructureException())

    private fun updateContext(
        foundUser: UserDetails,
        request: HttpServletRequest,
    ) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }
}
