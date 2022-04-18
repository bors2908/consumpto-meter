package com.example.consumpto.meter.dao

import com.example.consumpto.meter.domain.FuelRefill
import javax.validation.Valid
import org.springframework.validation.annotation.Validated

@Validated
abstract class FuelRefillDao : Dao<FuelRefill> {
    @Valid
    abstract fun getAllRefillsSorted(driverId: Long? = null): List<@Valid FuelRefill>
}