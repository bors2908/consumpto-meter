package com.example.consumpto.meter.domain

import com.example.consumpto.meter.currencyScale
import com.fasterxml.jackson.annotation.JsonValue
import java.math.BigDecimal
import java.time.LocalDate
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits
import javax.validation.constraints.Positive

interface Entity {
    var id: Long?
    val date: LocalDate
}

open class FuelRefill(
    val fuelType: FuelType,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Fuel price per liter must be positive.")
    @field:Digits(integer = 12, fraction = 2)
    val pricePerLiter: BigDecimal,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Fuel amount must be positive.")
    @field:Digits(integer = 12, fraction = 2)
    val amount: BigDecimal,

    @field:Positive
    val driverId: Long,

    override val date: LocalDate,
) : Entity {
    override var id: Long? = null

    val cost get() = (this.amount * this.pricePerLiter).currencyScale()

    override fun toString(): String {
        return "FuelRefill(" +
                "fuelType=$fuelType, " +
                "pricePerLiter=$pricePerLiter, " +
                "amount=$amount, " +
                "driverId=$driverId, " +
                "date=$date, " +
                "id=$id" +
                ")"
    }
}

data class FuelStat(
    var amount: BigDecimal,
    var totalPrice: BigDecimal,
) {
    val avgPricePerLiter get() = (totalPrice / amount).currencyScale()
}

enum class FuelType(@JsonValue val type: String) {
    P98("98"),
    P95("95"),
    D("D")
}
