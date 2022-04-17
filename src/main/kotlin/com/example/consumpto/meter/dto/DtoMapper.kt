package com.example.consumpto.meter.dto

import com.example.consumpto.meter.currencyScale
import com.example.consumpto.meter.entities.FuelRefill
import org.springframework.stereotype.Component

@Component
class DtoMapper {
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
