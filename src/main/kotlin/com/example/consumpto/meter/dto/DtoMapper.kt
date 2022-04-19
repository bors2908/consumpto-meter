package com.example.consumpto.meter.dto

import com.example.consumpto.meter.domain.FuelRefill
import com.example.consumpto.meter.domain.FuelStat
import com.example.consumpto.meter.domain.FuelType
import com.example.consumpto.meter.toYearMonthLocalizedString
import java.time.YearMonth
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
        return map.mapKeys { it.key.toYearMonthLocalizedString() }
    }

    fun mapRefills(map: Map<YearMonth, List<FuelRefill>>): Map<String, List<RefillDto>> {
        return map.entries.associate { it.key.toYearMonthLocalizedString() to it.value.map { refill -> refillToDto(refill) } }
    }

    fun mapStats(map: Map<YearMonth, Map<FuelType, FuelStat>>): Map<String, List<StatDto>> {
        return map.entries.associate { it.key.toYearMonthLocalizedString() to statToDto(it.value) }
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
}
