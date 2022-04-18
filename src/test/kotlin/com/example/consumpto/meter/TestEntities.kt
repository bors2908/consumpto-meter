package com.example.consumpto.meter

import com.example.consumpto.meter.domain.FuelRefill
import com.example.consumpto.meter.domain.FuelType
import java.math.BigDecimal
import java.time.LocalDate

class TestFuelRefill(
    fuelType: FuelType,
    pricePerLiter: BigDecimal,
    amount: BigDecimal,
    driverId: Long,
    date: LocalDate
) : FuelRefill(
    fuelType,
    pricePerLiter,
    amount,
    driverId,
    date
) {
    constructor(
        fuelType: FuelType,
        pricePerLiter: Double,
        amount: Double,
        driverId: Long,
        date: LocalDate
    ) : this(
        fuelType,
        BigDecimal.valueOf(pricePerLiter),
        BigDecimal.valueOf(amount),
        driverId,
        date
    )
}