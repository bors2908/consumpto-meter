package com.example.consumpto.meter.dao

import com.example.consumpto.meter.domain.FuelRefill

abstract class FuelRefillDao : Dao<FuelRefill> {
    abstract fun getAllRefillsSorted(driverId: Long?): List<FuelRefill>
}