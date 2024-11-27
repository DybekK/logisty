package com.logisty.core.application.security

import com.logisty.core.application.security.SecurityErrorCode.BAD_CREDENTIALS
import com.logisty.core.application.security.SecurityErrorCode.TOKEN_EXPIRED_OR_NOT_FOUND
import com.logisty.core.application.security.SecurityErrorCode.INVALID_TOKEN_STRUCTURE
import org.springframework.security.core.AuthenticationException

open class SecurityException(
    message: SecurityErrorCode,
) : AuthenticationException(message.name)

enum class SecurityErrorCode {
    BAD_CREDENTIALS,
    INVALID_TOKEN_STRUCTURE,
    TOKEN_EXPIRED_OR_NOT_FOUND,
}

object SecurityExceptions {
    class UserBadCredentialsException : SecurityException(BAD_CREDENTIALS)

    class InvalidTokenStructureException : SecurityException(INVALID_TOKEN_STRUCTURE)

    class TokenExpiredOrNotFoundException : SecurityException(TOKEN_EXPIRED_OR_NOT_FOUND)
}
