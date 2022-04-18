package com.example.consumpto.meter.unit

import com.example.consumpto.meter.ConsumptoMeterService
import com.example.consumpto.meter.TestFuelRefill
import com.example.consumpto.meter.dao.FuelRefillTestDao
import com.example.consumpto.meter.getRandomDriver
import com.example.consumpto.meter.getRandomFuelAmount
import com.example.consumpto.meter.getRandomFuelPrice
import com.example.consumpto.meter.getRandomFuelType
import com.example.consumpto.meter.getRandomLocalDate
import java.time.YearMonth
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConsumptoMeterServiceUnitTest {
    private val testDao = FuelRefillTestDao()
    private val meterService = ConsumptoMeterService(testDao)

    private fun getTestFuelRefillsData() = (1..1000).map {
        TestFuelRefill(
            getRandomFuelType(),
            getRandomFuelPrice(),
            getRandomFuelAmount(),
            getRandomDriver(),
            getRandomLocalDate()
        )
    }

    @AfterEach
    fun tearDown() {
        testDao.deleteAll()
    }

    @Test
    fun testGetTotalCost() {
        val testData = getTestFuelRefillsData()

        val sum = testData
            .map { it.cost }
            .reduce { acc, subSum -> acc + subSum }

        val months = testData
            .map { YearMonth.of(it.date.year, it.date.month) }
            .distinct()

        meterService.addRefills(testData)

        val costByMonth = meterService.getCostByMonth()

        assertTrue(costByMonth.keys.containsAll(months))

        val resultingSum = costByMonth.values.reduce { acc, subSum -> acc + subSum }

        assertEquals(sum, resultingSum)
    }

    @Test
    fun testGetStats() {
        val testData = getTestFuelRefillsData()

        val sum = testData
            .map { it.cost }
            .reduce { acc, subSum -> acc + subSum }

        val months = testData
            .map { YearMonth.of(it.date.year, it.date.month) }
            .distinct()

        meterService.addRefills(testData)

        val statsByMonth = meterService.getStatsByMonth()

        assertTrue(statsByMonth.keys.containsAll(months))

        val resultingSum = statsByMonth.values
            .flatMap { it.values }
            .map { it.totalPrice }
            .reduce { acc, subSum -> acc + subSum }

        assertEquals(sum, resultingSum)
    }

    @Test
    fun testGetRefills() {
        val testData = getTestFuelRefillsData()

        val months = testData
            .map { YearMonth.of(it.date.year, it.date.month) }
            .distinct()

        meterService.addRefills(testData)

        val refillsByMonth = meterService.getRefillsByMonth()

        assertTrue(refillsByMonth.keys.containsAll(months))

        val refills = refillsByMonth.values.flatten()

        assertTrue(refills.containsAll(testData))
    }

    @Test
    fun addInvalidRefills() {

    }
}