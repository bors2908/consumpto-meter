package com.example.consumpto.meter.service

import com.example.consumpto.meter.dao.FuelRefillDao
import com.example.consumpto.meter.domain.FuelRefill
import org.springframework.stereotype.Service

@Service
class NewRefillService(
    private val refillDao: FuelRefillDao,
) {
    fun newRefills(refills: List<FuelRefill>): List<Long> {
        return refillDao.addAll(refills)
    }
}
