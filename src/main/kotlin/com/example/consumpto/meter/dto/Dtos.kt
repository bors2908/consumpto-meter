package com.example.consumpto.meter.dto

import com.example.consumpto.meter.entities.FuelType
import java.math.BigDecimal
import java.time.LocalDate

data class RefillDTO(
    val fuelType: FuelType,
    val amount: BigDecimal,
    val pricePerLiter: BigDecimal,
    val totalPrice: BigDecimal
)

data class StatDTO(
    val fuelType: FuelType,
    val amount: BigDecimal,
    val avgPricePerLiter: BigDecimal,
    val totalPrice: BigDecimal
)

data class AddRefillDTO(
    val fuelType: FuelType,
    val pricePerLiter: BigDecimal,
    val amount: BigDecimal,
    val date: LocalDate,
    val driverId: Long,
)
