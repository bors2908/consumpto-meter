package com.example.consumpto.meter

import com.example.consumpto.meter.entities.FuelRefill
import com.example.consumpto.meter.entities.FuelType
import java.math.BigDecimal
import java.time.LocalDate

class TestFuelRefill(
    fuelType: FuelType,
    pricePerLiter: Double,
    amount: Double,
    date: LocalDate,
    driverId: Long,
) : FuelRefill(
    fuelType,
    BigDecimal.valueOf(pricePerLiter),
    BigDecimal.valueOf(amount),
    driverId,
    date
)