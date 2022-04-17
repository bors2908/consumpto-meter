package com.example.consumpto.meter.entities

import com.fasterxml.jackson.annotation.JsonValue
import java.math.BigDecimal
import java.time.LocalDate

interface Entity {
    var id: Long?
    val date: LocalDate
}

open class FuelRefill(
    val fuelType: FuelType,
    val pricePerLiter: BigDecimal,
    val amount: BigDecimal,
    val driverId: Long,
    override val date: LocalDate,
) : Entity {
    override var id: Long? = null
}

enum class FuelType(@JsonValue val type: String) {
    P98("98"),
    P95("95"),
    D("D")
}
