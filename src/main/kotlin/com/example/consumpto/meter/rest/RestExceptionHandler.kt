package com.example.consumpto.meter.rest

import javax.validation.ConstraintViolationException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

private val log = KotlinLogging.logger {  }

@ControllerAdvice
class RestExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleBadRequest(ex: ConstraintViolationException) {
        log.error("Invalid account supplied in request")
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleGeneralError(ex: Exception) {
        log.error("An error occurred processing request:$ex")
    }

}