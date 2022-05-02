package com.example.consumpto.meter.dto

import com.example.consumpto.meter.domain.FuelType
import java.math.BigDecimal
import java.time.LocalDate
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits
import javax.validation.constraints.Positive
import org.springframework.format.annotation.DateTimeFormat

data class RefillDto(
    val fuelType: FuelType,
    val amount: BigDecimal,
    val pricePerLiter: BigDecimal,
    val totalPrice: BigDecimal,
)

data class StatDto(
    val fuelType: FuelType,
    val amount: BigDecimal,
    val avgPricePerLiter: BigDecimal,
    val totalPrice: BigDecimal,
)

data class NewRefillDto(
    val fuelType: FuelType,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Fuel price per liter must be positive.")
    @field:Digits(integer = 12, fraction = 2)
    val pricePerLiter: BigDecimal,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Fuel amount must be positive.")
    @field:Digits(integer = 12, fraction = 2)
    val amount: BigDecimal,

    @field:DateTimeFormat
    val date: LocalDate,

    @field:Positive
    val driverId: Long
)

data class ErrorDto(val error: String)
