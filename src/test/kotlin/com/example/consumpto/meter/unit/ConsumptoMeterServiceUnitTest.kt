package com.example.consumpto.meter.unit

import com.example.consumpto.meter.ConsumptoMeterService
import com.example.consumpto.meter.dao.FuelRefillTestDao
import com.example.consumpto.meter.TestFuelRefill
import com.example.consumpto.meter.domain.FuelType.D
import com.example.consumpto.meter.domain.FuelType.P95
import com.example.consumpto.meter.domain.FuelType.P98
import java.time.LocalDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConsumptoMeterServiceUnitTest {
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

    @AfterEach
    fun tearDown() {
        testDao.deleteAll()
    }

    @Test
    fun testGetTotalCost() {
        meterService.getCostByMonth().forEach { println(it) }
    }

    @Test
    fun testGetStats() {
        meterService.getStatsByMonth().forEach { println(it) }
    }

    @Test
    fun testGetRefills() {
        meterService.getRefillsByMonth().forEach{ println(it) }
    }

    @Test
    fun addRefills() {

    }
}