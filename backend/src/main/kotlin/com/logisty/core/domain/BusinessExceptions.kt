package com.logisty.core.domain

import com.logisty.core.domain.ErrorCode.FLEET_ALREADY_EXISTS
import com.logisty.core.domain.ErrorCode.FLEET_NOT_FOUND
import com.logisty.core.domain.ErrorCode.INVITATION_ALREADY_ACCEPTED
import com.logisty.core.domain.ErrorCode.INVITATION_ALREADY_EXISTS
import com.logisty.core.domain.ErrorCode.INVITATION_EXPIRED
import com.logisty.core.domain.ErrorCode.INVITATION_NOT_FOUND
import com.logisty.core.domain.ErrorCode.USER_ALREADY_EXISTS

open class BusinessException(
    message: ErrorCode,
) : RuntimeException(message.name)

enum class ErrorCode {
    // fleet
    FLEET_NOT_FOUND,
    FLEET_ALREADY_EXISTS,

    // user
    USER_ALREADY_EXISTS,

    // invitation
    INVITATION_NOT_FOUND,
    INVITATION_ALREADY_EXISTS,
    INVITATION_ALREADY_ACCEPTED,
    INVITATION_EXPIRED,
}

object BusinessExceptions {
    // fleet
    class FleetNotFoundException : BusinessException(FLEET_NOT_FOUND)

    class FleetAlreadyExistsException : BusinessException(FLEET_ALREADY_EXISTS)

    // user
    class UserAlreadyExistsException : BusinessException(USER_ALREADY_EXISTS)

    // invitation
    class InvitationNotFoundException : BusinessException(INVITATION_NOT_FOUND)

    class InvitationAlreadyExistsException : BusinessException(INVITATION_ALREADY_EXISTS)

    class InvitationAlreadyAcceptedException : BusinessException(INVITATION_ALREADY_ACCEPTED)

    class InvitationExpiredException : BusinessException(INVITATION_EXPIRED)
}
