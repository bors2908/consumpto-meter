package com.example.consumpto.meter

import com.example.consumpto.meter.entities.FuelType.D
import com.example.consumpto.meter.entities.FuelType.P95
import com.example.consumpto.meter.entities.FuelType.P98
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestLogic() {

    private val testDao = FuelRefillTestDao()
    private val meterService = ConsumptoMeterService(testDao)

    @BeforeEach
    fun setUp() {
        testDao.addAll(listOf(
            TestFuelRefill(P95, 1.8, 50.0, LocalDate.now(), 1),
            TestFuelRefill(P98, 1.8, 50.0, LocalDate.now().minusMonths(1), 2),
            TestFuelRefill(D, 1.8, 50.0, LocalDate.now().minusMonths(6), 3),
            TestFuelRefill(P95, 1.8, 50.0, LocalDate.now().minusYears(1), 4),
            TestFuelRefill(P95, 1.8, 50.0, LocalDate.now().minusYears(1).minusMonths(1), 5),
            TestFuelRefill(P95, 1.8, 50.0, LocalDate.now(), 1),
            TestFuelRefill(P98, 1.8, 50.0, LocalDate.now().minusMonths(1), 2),
            TestFuelRefill(D, 1.8, 50.0, LocalDate.now().minusMonths(6), 3),
            TestFuelRefill(P95, 1.8, 50.0, LocalDate.now().minusYears(1), 4),
            TestFuelRefill(P95, 1.8, 50.0, LocalDate.now().minusYears(1).minusMonths(1), 5),
        ))
    }

    @Test
    fun testMonthly() {
        meterService.getCostByMonth().forEach{ println(it) }
        meterService.getStatsByMonth().forEach{ println(it) }
    }
}