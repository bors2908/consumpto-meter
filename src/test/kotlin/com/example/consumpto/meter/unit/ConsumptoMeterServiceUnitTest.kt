package com.example.consumpto.meter.unit

import com.example.consumpto.meter.ConsumptoMeterService
import com.example.consumpto.meter.dao.FuelRefillTestDao
import com.example.consumpto.meter.domain.FuelRefill
import com.example.consumpto.meter.getRandomDriver
import com.example.consumpto.meter.getRandomFuelAmount
import com.example.consumpto.meter.getRandomFuelPrice
import com.example.consumpto.meter.getRandomFuelType
import com.example.consumpto.meter.getRandomLocalDate
import com.example.consumpto.meter.getRandomizedRefill
import java.time.YearMonth
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConsumptoMeterServiceUnitTest {
    private val testDao = FuelRefillTestDao()
    private val meterService = ConsumptoMeterService(testDao)

    private fun getTestFuelRefillsData() = (1..300).map { getRandomizedRefill() }

    @AfterEach
    fun tearDown() {
        testDao.deleteAll()
    }

    @Test
    fun testGetTotalCost() {
        checkTotalCost()
    }

    @Test
    fun testGetTotalCostDriverId() {
        checkTotalCost(getRandomDriver())
    }

    @Test
    fun testGetStats() {
        checkGetStats()
    }

    @Test
    fun testGetStatsDriverId() {
        checkGetStats(getRandomDriver())
    }

    @Test
    fun testGetRefills() {
        checkGetRefills()
    }

    @Test
    fun testGetRefillsDriverId() {
        checkGetRefills(getRandomDriver())
    }

    @Test
    fun addInvalidRefills() {
        meterService.addRefills(listOf(
            FuelRefill(
                getRandomFuelType(),
                getRandomFuelPrice().negate(),
                getRandomFuelAmount().negate(),
                -getRandomDriver(),
                getRandomLocalDate()
            )
        ))
    }

    private fun checkTotalCost(driverId: Long? = null) {
        val testData = getTestFuelRefillsData()

        val filteredTestData = testData.filter { driverId == null || it.driverId == driverId }

        val sum = filteredTestData
            .map { it.cost }
            .reduce { acc, subSum -> acc + subSum }

        val months = filteredTestData
            .map { YearMonth.of(it.date.year, it.date.month) }
            .distinct()

        meterService.addRefills(testData)

        val costByMonth = meterService.getCostByMonth(driverId)

        assertTrue(costByMonth.keys.containsAll(months))

        val resultingSum = costByMonth.values.reduce { acc, subSum -> acc + subSum }

        assertEquals(sum, resultingSum)
    }

    private fun checkGetStats(driverId: Long? = null) {
        val testData = getTestFuelRefillsData()

        val filteredTestData = testData.filter { driverId == null || it.driverId == driverId }

        val sum = filteredTestData
            .map { it.cost }
            .reduce { acc, subSum -> acc + subSum }

        val months = filteredTestData
            .map { YearMonth.of(it.date.year, it.date.month) }
            .distinct()

        meterService.addRefills(testData)

        val statsByMonth = meterService.getStatsByMonth(driverId)

        assertTrue(statsByMonth.keys.containsAll(months))

        val resultingSum = statsByMonth.values
            .flatMap { it.values }
            .map { it.totalPrice }
            .reduce { acc, subSum -> acc + subSum }

        assertEquals(sum, resultingSum)
    }

    private fun checkGetRefills(driverId: Long? = null) {
        val testData = getTestFuelRefillsData()

        val filteredTestData = testData.filter { driverId == null || it.driverId == driverId }

        val months = filteredTestData
            .map { YearMonth.of(it.date.year, it.date.month) }
            .distinct()

        meterService.addRefills(testData)

        val refillsByMonth = meterService.getRefillsByMonth(driverId)

        assertTrue(refillsByMonth.keys.containsAll(months))

        val refills = refillsByMonth.values.flatten()

        assertTrue(refills.containsAll(filteredTestData))
    }
}
