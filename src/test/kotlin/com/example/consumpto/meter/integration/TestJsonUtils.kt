package com.example.consumpto.meter.integration

import com.example.consumpto.meter.domain.FuelType
import com.example.consumpto.meter.getRandomDriver
import com.example.consumpto.meter.getRandomFuelAmount
import com.example.consumpto.meter.getRandomFuelPrice
import com.example.consumpto.meter.getRandomFuelType
import com.example.consumpto.meter.getRandomLocalDate
import java.math.BigDecimal
import java.time.LocalDate
import org.json.JSONArray
import org.json.JSONObject

fun getJsonRequestData(): JSONArray {
    val requestData = JSONArray((0..10).map {
        getRequestJsonObject()
    })
    return requestData
}

fun getRequestJsonObject(
    fuelType: FuelType = getRandomFuelType(),
    pricePerLiter: BigDecimal = getRandomFuelPrice(),
    amount: BigDecimal = getRandomFuelAmount(),
    date: LocalDate = getRandomLocalDate(),
    driverId: Long = getRandomDriver(),
) = JSONObject(mapOf(
    "fuelType" to fuelType.type,
    "pricePerLiter" to pricePerLiter,
    "amount" to amount,
    "driverId" to driverId,
    "date" to date
))