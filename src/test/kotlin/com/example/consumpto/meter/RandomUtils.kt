package com.example.consumpto.meter

import com.example.consumpto.meter.domain.FuelType
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.random.Random

val random = Random(31)

fun getRandomFuelType(): FuelType = FuelType.values()[random.nextInt(1, 3)]
fun getRandomFuelPrice(): BigDecimal = BigDecimal(random.nextDouble(0.5, 2.9)).currencyScale()
fun getRandomFuelAmount(): BigDecimal = BigDecimal(random.nextDouble(5.0, 100.0)).currencyScale()
fun getRandomDriver(): Long = random.nextLong(1, 20)
fun getRandomLocalDate(): LocalDate = LocalDate.of(
    random.nextInt(2010, 2022),
    random.nextInt(1, 12),
    random.nextInt(1, 28)
)

fun getRandomizedRefill() = TestFuelRefill(
    getRandomFuelType(),
    getRandomFuelPrice(),
    getRandomFuelAmount(),
    getRandomDriver(),
    getRandomLocalDate()
)
