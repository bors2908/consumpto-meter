package com.example.consumpto.meter

import com.example.consumpto.meter.dao.FuelRefillDao
import com.example.consumpto.meter.entities.Entity
import com.example.consumpto.meter.entities.FuelRefill
import com.example.consumpto.meter.entities.FuelStat
import com.example.consumpto.meter.entities.FuelType
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

    fun getStatsByMonth(driverId: Long? = null): Map<String, Map<FuelType, FuelStat>> {
        val allRefills = refillDao.getAllRefillsSorted(driverId)

        return getMonthlyMap(
            sortedEntities = allRefills,
            newAcc = {
                FuelType.values()
                    .associateWith { FuelStat(BigDecimal.ZERO, BigDecimal.ZERO) }
                    .toMutableMap()
            },
            addToAcc = { acc, entity -> acc.processRefillAndReturnMap(entity) }
        )
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

        // Add everything left as the last month.
        val lastDate = sortedEntities.last().date

        monthly[getMonthString(lastDate)] = acc

        return monthly
    }

    private fun getMonthString(date: LocalDate): String {
        val monthName = date.month
            .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            .capitalize()

        return "$monthName ${date.year}"
    }

    private fun <E> MutableList<E>.addAndReturnList(element: E): MutableList<E> {
        this.add(element)

        return this
    }

    private fun MutableMap<FuelType, FuelStat>.processRefillAndReturnMap(refill: FuelRefill): MutableMap<FuelType, FuelStat> {
        val stat = this.getValue(refill.fuelType)

        stat.amount += refill.amount
        stat.totalPrice += refill.getCost()

        return this
    }

    private fun FuelRefill.getCost() = (this.amount * this.pricePerLiter).currencyScale()
}
