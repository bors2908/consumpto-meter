package com.example.consumpto.meter.dto

import com.example.consumpto.meter.currencyScale
import com.example.consumpto.meter.domain.FuelRefill
import com.example.consumpto.meter.domain.FuelStat
import com.example.consumpto.meter.domain.FuelType
import org.springframework.stereotype.Component

@Component
class DtoMapper {
    fun statToDto(stats: Map<FuelType, FuelStat>): List<StatDTO> {
        return stats
            .map {
            val stat = it.value

            StatDTO(
                it.key,
                stat.amount,
                (stat.totalPrice / stat.amount).currencyScale(),
                stat.totalPrice
            )
        }
    }

    fun refillToDto(refill: FuelRefill): RefillDTO {
        return RefillDTO(
            refill.fuelType,
            refill.amount,
            refill.pricePerLiter,
            (refill.pricePerLiter * refill.amount).currencyScale()
        )
    }

    fun addRefillDtoToRefill(addRefillDTO: AddRefillDTO): FuelRefill {
        return FuelRefill(
            addRefillDTO.fuelType,
            addRefillDTO.pricePerLiter,
            addRefillDTO.amount,
            addRefillDTO.driverId,
            addRefillDTO.date
        )
    }
}
