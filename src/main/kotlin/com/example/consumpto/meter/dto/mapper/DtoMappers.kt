package com.example.consumpto.meter.dto.mapper

import com.example.consumpto.meter.domain.FuelRefill
import com.example.consumpto.meter.domain.FuelStat
import com.example.consumpto.meter.domain.FuelType
import com.example.consumpto.meter.dto.NewRefillDto
import com.example.consumpto.meter.dto.RefillDto
import com.example.consumpto.meter.dto.StatDto
import com.example.consumpto.meter.toYearMonthLocalizedString
import java.math.BigDecimal
import java.time.YearMonth
import org.springframework.stereotype.Component

@Component
class NewRefillDtoMapper : DtoMapper<List<NewRefillDto>, List<FuelRefill>>() {
    override val typeSignature = TypeSignature<List<NewRefillDto>, List<FuelRefill>>()

    override fun map(from: List<NewRefillDto>): List<FuelRefill> {
        return from.map { newRefillDtoToRefill(it) }
    }

    private fun newRefillDtoToRefill(newRefillDto: NewRefillDto): FuelRefill {
        return FuelRefill(
            newRefillDto.fuelType,
            newRefillDto.pricePerLiter,
            newRefillDto.amount,
            newRefillDto.driverId,
            newRefillDto.date
        )
    }
}

@Component
class StatDtoMapper : DtoMapper<Map<YearMonth, Map<FuelType, FuelStat>>, Map<String, List<StatDto>>>() {
    override val typeSignature = TypeSignature<Map<YearMonth, Map<FuelType, FuelStat>>, Map<String, List<StatDto>>>()

    override fun map(from: Map<YearMonth, Map<FuelType, FuelStat>>): Map<String, List<StatDto>> {
        return from.entries.associate { it.key.toYearMonthLocalizedString() to statToDto(it.value) }
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

@Component
class RefillDtoMapper : DtoMapper<Map<YearMonth, List<FuelRefill>>, Map<String, List<RefillDto>>>() {
    override val typeSignature = TypeSignature<Map<YearMonth, List<FuelRefill>>, Map<String, List<RefillDto>>>()

    override fun map(from: Map<YearMonth, List<FuelRefill>>): Map<String, List<RefillDto>> {
        return from.entries
            .associate { it.key.toYearMonthLocalizedString() to it.value.map { refill -> refillToDto(refill) } }
    }

    private fun refillToDto(refill: FuelRefill): RefillDto {
        return RefillDto(
            refill.fuelType,
            refill.amount,
            refill.pricePerLiter,
            refill.cost
        )
    }
}

@Component
class MonthlyBigDecimalDtoMapper : MapMonthKeyDtoMapper<BigDecimal>() {
    override val typeSignature = TypeSignature<Map<YearMonth, BigDecimal>, Map<String, BigDecimal>>()
}

abstract class MapMonthKeyDtoMapper<V: Any> : DtoMapper<Map<YearMonth, V>, Map<String, V>>() {
    override fun map(from: Map<YearMonth, V>): Map<String, V> {
        return from.mapKeys { it.key.toYearMonthLocalizedString() }
    }
}
