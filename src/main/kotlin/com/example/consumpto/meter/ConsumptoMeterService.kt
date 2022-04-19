package com.example.consumpto.meter

import com.example.consumpto.meter.dao.FuelRefillDao
import com.example.consumpto.meter.domain.Entity
import com.example.consumpto.meter.domain.FuelRefill
import com.example.consumpto.meter.domain.FuelStat
import com.example.consumpto.meter.domain.FuelType
import java.math.BigDecimal
import java.time.YearMonth
import org.springframework.stereotype.Service

@Service
class ConsumptoMeterService(
    private val refillDao: FuelRefillDao,
) {
    fun getCostByMonth(driverId: Long? = null): Map<YearMonth, BigDecimal> {
        val allRefills = refillDao.getAllRefillsSorted(driverId)

        return getMonthlyMap(
            sortedEntities = allRefills,
            newAcc = { BigDecimal.ZERO },
            addToAcc = { acc, entity -> acc.add(entity.cost) }
        )
    }

    fun getRefillsByMonth(driverId: Long? = null): Map<YearMonth, List<FuelRefill>> {
        val allRefills = refillDao.getAllRefillsSorted(driverId)

        return getMonthlyMap(
            sortedEntities = allRefills,
            newAcc = { mutableListOf() },
            addToAcc = { acc, entity -> acc.addAndReturnList(entity) }
        )
    }

    fun getStatsByMonth(driverId: Long? = null): Map<YearMonth, Map<FuelType, FuelStat>> {
        val allRefills = refillDao.getAllRefillsSorted(driverId)

        return getMonthlyMap(
            sortedEntities = allRefills,
            newAcc = { mutableMapOf() },
            addToAcc = { acc, entity -> acc.processRefillAndReturnMap(entity) }
        )
    }

    fun addRefills(refills: List<FuelRefill>): List<Long> {
        return refillDao.addAll(refills)
    }

    // Processes a list of entities with date and groups it by month.
    private inline fun <reified T, reified R : Entity> getMonthlyMap(
        sortedEntities: List<R>,
        newAcc: () -> T,
        addToAcc: (acc: T, entity: R) -> T,
    ): Map<YearMonth, T> {
        if (sortedEntities.isEmpty()) return emptyMap()

        val monthly = mutableMapOf<YearMonth, T>()

        var acc = newAcc.invoke()

        var prevMonth = sortedEntities.first().date.toYearMonth()

        for (entity in sortedEntities) {
            val month = entity.date.toYearMonth()

            if (month != prevMonth) {
                monthly[prevMonth] = acc

                acc = newAcc.invoke()
            }

            acc = addToAcc.invoke(acc, entity)

            prevMonth = month
        }

        // Add everything left as the last month.
        val lastMonth = sortedEntities.last().date.toYearMonth()

        monthly[lastMonth] = acc

        return monthly
    }

    private fun <E> MutableList<E>.addAndReturnList(element: E): MutableList<E> {
        this.add(element)

        return this
    }

    private fun MutableMap<FuelType, FuelStat>.processRefillAndReturnMap(refill: FuelRefill): MutableMap<FuelType, FuelStat> {
        val stat = this.getOrPut(refill.fuelType) { FuelStat(BigDecimal.ZERO, BigDecimal.ZERO) }

        stat.amount += refill.amount
        stat.totalPrice += refill.cost

        return this
    }
}
