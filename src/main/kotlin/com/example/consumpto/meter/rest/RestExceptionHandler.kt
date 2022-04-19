package com.example.consumpto.meter.rest

import com.example.consumpto.meter.dto.ErrorDto
import javax.validation.ConstraintViolationException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

private val log = KotlinLogging.logger {}

@ControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleBadRequest(ex: ConstraintViolationException): ResponseEntity<Any> {
        val message = "Input constraints were violated: [${ex.message}]"

        log.error(message)

        return ResponseEntity(ErrorDto(message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleBadRequest(ex: HttpMessageNotReadableException): ResponseEntity<Any> {
        val message = "Illegal input: [${ex.message}]"

        log.error(message)

        return ResponseEntity(ErrorDto(message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralError(ex: Exception): ResponseEntity<Any> {
        val message = "An error occurred processing request:$ex"

        log.error(message)

        return ResponseEntity(ErrorDto(message), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
