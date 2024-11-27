package com.logisty.core.adapter

import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ErrorResponse(
    val errors: List<String?>,
)

fun Throwable.toErrorResponse(): ErrorResponse = ErrorResponse(listOf(message))

fun Throwable.toBadRequestResponseEntity(): ResponseEntity<ErrorResponse> =
    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        toErrorResponse(),
    )

fun Throwable.toUnauthorizedResponseEntity(): ResponseEntity<ErrorResponse> =
    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
        ErrorResponse(listOf(message ?: "Invalid credentials")),
    )

fun Throwable.toNotFoundResponseEntity(): ResponseEntity<ErrorResponse> =
    ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        ErrorResponse(listOf(message)),
    )

fun Throwable.toInternalServerErrorResponseEntity(logger: Logger): ResponseEntity<ErrorResponse> =
    logger.error(message, this).let {
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(listOf("INTERNAL_SERVER_ERROR")),
        )
    }
