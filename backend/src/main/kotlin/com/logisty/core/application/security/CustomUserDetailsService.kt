package com.logisty.core.application.security

import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.port.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.logisty.core.domain.model.User as ApplicationUser

@Service
@Transactional
class CustomUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails =
        userRepository
            .findByEmail(UserEmail(email))
            ?.toUserDetails()
            ?: throw UsernameNotFoundException("User not found")

    private fun ApplicationUser.toUserDetails(): UserDetails =
        User
            .builder()
            .username(email.value)
            .password(password.value)
            .authorities(roles.map { SimpleGrantedAuthority(it.name) })
            .build()
}
