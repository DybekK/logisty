package com.logisty.core.domain

import com.logisty.core.domain.ErrorCode.FLEET_ALREADY_EXISTS
import com.logisty.core.domain.ErrorCode.FLEET_NOT_FOUND
import com.logisty.core.domain.ErrorCode.INVITATION_ALREADY_ACCEPTED
import com.logisty.core.domain.ErrorCode.INVITATION_ALREADY_EXISTS
import com.logisty.core.domain.ErrorCode.INVITATION_EXPIRED
import com.logisty.core.domain.ErrorCode.INVITATION_NOT_FOUND
import com.logisty.core.domain.ErrorCode.ORDER_ESTIMATED_START_TIME_AFTER_END_TIME
import com.logisty.core.domain.ErrorCode.ORDER_NOT_FOUND
import com.logisty.core.domain.ErrorCode.ORDER_STEP_NOT_FOUND
import com.logisty.core.domain.ErrorCode.STEP_ESTIMATED_ARRIVAL_TIME_IN_FUTURE
import com.logisty.core.domain.ErrorCode.USER_ALREADY_EXISTS
import com.logisty.core.domain.ErrorCode.USER_IS_NOT_DISPATCHER
import com.logisty.core.domain.ErrorCode.USER_IS_NOT_DRIVER
import com.logisty.core.domain.ErrorCode.USER_NOT_FOUND

open class BusinessException(
    message: ErrorCode,
) : RuntimeException(message.name)

enum class ErrorCode {
    // fleet
    FLEET_NOT_FOUND,
    FLEET_ALREADY_EXISTS,

    // user
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,

    // invitation
    INVITATION_NOT_FOUND,
    INVITATION_ALREADY_EXISTS,
    INVITATION_ALREADY_ACCEPTED,
    INVITATION_EXPIRED,

    // order
    ORDER_NOT_FOUND,
    ORDER_STEP_NOT_FOUND,
    USER_IS_NOT_DISPATCHER,
    USER_IS_NOT_DRIVER,
    STEP_ESTIMATED_ARRIVAL_TIME_IN_FUTURE,
    ORDER_ESTIMATED_START_TIME_AFTER_END_TIME,
}

object BusinessExceptions {
    // fleet
    class FleetNotFoundException : BusinessException(FLEET_NOT_FOUND)

    class FleetAlreadyExistsException : BusinessException(FLEET_ALREADY_EXISTS)

    // user
    class UserNotFoundException : BusinessException(USER_NOT_FOUND)

    class UserAlreadyExistsException : BusinessException(USER_ALREADY_EXISTS)

    // invitation
    class InvitationNotFoundException : BusinessException(INVITATION_NOT_FOUND)

    class InvitationAlreadyExistsException : BusinessException(INVITATION_ALREADY_EXISTS)

    class InvitationAlreadyAcceptedException : BusinessException(INVITATION_ALREADY_ACCEPTED)

    class InvitationExpiredException : BusinessException(INVITATION_EXPIRED)

    // order
    class OrderNotFoundException : BusinessException(ORDER_NOT_FOUND)

    class OrderStepNotFoundException : BusinessException(ORDER_STEP_NOT_FOUND)

    class UserIsNotDriverException : BusinessException(USER_IS_NOT_DRIVER)

    class UserIsNotDispatcherException : BusinessException(USER_IS_NOT_DISPATCHER)

    class StepEstimatedArrivalTimeInFutureException : BusinessException(STEP_ESTIMATED_ARRIVAL_TIME_IN_FUTURE)

    class OrderEstimatedStartTimeAfterEndTimeException : BusinessException(ORDER_ESTIMATED_START_TIME_AFTER_END_TIME)
}
