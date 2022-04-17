package com.example.consumpto.meter

import com.example.consumpto.meter.dao.FuelRefillDao
import com.example.consumpto.meter.entities.Entity
import com.example.consumpto.meter.entities.FuelRefill
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import org.springframework.stereotype.Service

@Service
class ConsumptoMeterService(
    private val refillDao: FuelRefillDao,
) {
    fun getCostByMonth(driverId: Long? = null): Map<String, BigDecimal> {
        val allRefills = refillDao.getAllRefillsSorted(driverId)

        return getMonthlyMap(
            sortedEntities = allRefills,
            newAcc = { BigDecimal.ZERO },
            addToAcc = { acc, entity -> acc.add(entity.getCost()) }
        )
    }

    fun getRefillsByMonth(driverId: Long? = null): Map<String, MutableList<FuelRefill>> {
        val allRefills = refillDao.getAllRefillsSorted(driverId)

        return getMonthlyMap(
            sortedEntities = allRefills,
            newAcc = { mutableListOf() },
            addToAcc = { acc, entity -> acc.addAndReturnList(entity) }
        )
    }

    fun addRefill(refill: FuelRefill): Long {
        return refillDao.add(refill)
    }

    fun addRefills(refills: List<FuelRefill>): List<Long> {
        return refillDao.addAll(refills)
    }

    private inline fun <reified T, reified R : Entity> getMonthlyMap(
        sortedEntities: List<R>,
        newAcc: () -> T,
        addToAcc: (acc: T, entity: R) -> T,
    ): Map<String, T> {
        if (sortedEntities.isEmpty()) return emptyMap()

        val monthly = mutableMapOf<String, T>()

        var acc = newAcc.invoke()

        var prevDate = sortedEntities.first().date

        for (entity in sortedEntities) {
            val date = entity.date

            if (date.year != prevDate.year || date.month != prevDate.month) {
                monthly[getMonthString(prevDate)] = acc

                acc = newAcc.invoke()
            }

            acc = addToAcc.invoke(acc, entity)

            prevDate = date
        }

        val lastDate = sortedEntities.last().date

        monthly[getMonthString(lastDate)] = acc

        return monthly
    }

    private fun getMonthString(date: LocalDate): String {
        val monthName = date.month
            .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            .capitalize(Locale.getDefault())

        return "$monthName ${date.year}"
    }

    private fun <E> MutableList<E>.addAndReturnList(element: E): MutableList<E> {
        this.add(element)

        return this
    }

    private fun FuelRefill.getCost() = this.amount * this.pricePerLiter
}
