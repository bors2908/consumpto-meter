package com.example.consumpto.meter.dto

import com.example.consumpto.meter.capitalize
import com.example.consumpto.meter.domain.FuelRefill
import com.example.consumpto.meter.domain.FuelStat
import com.example.consumpto.meter.domain.FuelType
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import org.springframework.stereotype.Component

@Component
class DtoMapper {
    fun addRefillDtoToRefill(addRefillDTO: AddRefillDto): FuelRefill {
        return FuelRefill(
            addRefillDTO.fuelType,
            addRefillDTO.pricePerLiter,
            addRefillDTO.amount,
            addRefillDTO.driverId,
            addRefillDTO.date
        )
    }

    fun <V> mapMonthKeys(map: Map<YearMonth, V>): Map<String, V> {
        return map.mapKeys { yearMonthToString(it.key) }
    }

    fun mapRefills(map: Map<YearMonth, List<FuelRefill>>): Map<String, List<RefillDto>> {
        return map.entries.associate { yearMonthToString(it.key) to it.value.map { refill -> refillToDto(refill) } }
    }

    fun mapStats(map: Map<YearMonth, Map<FuelType, FuelStat>>): Map<String, List<StatDto>> {
        return map.entries.associate { yearMonthToString(it.key) to statToDto(it.value) }
    }

    private fun refillToDto(refill: FuelRefill): RefillDto {
        return RefillDto(
            refill.fuelType,
            refill.amount,
            refill.pricePerLiter,
            refill.cost
        )
    }

    private fun statToDto(stats: Map<FuelType, FuelStat>): List<StatDto> {
        return stats
            .map {
                val stat = it.value

                StatDto(
                    it.key,
                    stat.amount,
                    stat.avgPricePerLiter,
                    stat.totalPrice
                )
            }
    }

    private fun yearMonthToString(month: YearMonth): String {
        val monthName = month.month
            .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            .capitalize()

        return "$monthName ${month.year}"
    }
}
