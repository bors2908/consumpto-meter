package com.example.consumpto.meter

import com.example.consumpto.meter.dao.FuelRefillDao
import com.example.consumpto.meter.entities.FuelRefill

class FuelRefillTestDao: FuelRefillDao() {
    private val storage = TestStorage<FuelRefill>()

    override fun getAllRefillsSorted(driverId: Long?): List<FuelRefill> {
        return getAll()
            .filter { if (driverId == null) true else driverId == it.driverId }
            .sortedBy { it.date }
    }

    override fun getAll(): Collection<FuelRefill> {
        return storage.getAll()
    }

    override fun get(id: Long): FuelRefill? {
        return storage.get(id)
    }

    override fun add(t: FuelRefill): Long {
        return storage.add(t)
    }

    override fun addAll(collection: Collection<FuelRefill>): List<Long> {
        return storage.addAll(collection)
    }

    override fun update(t: FuelRefill): Boolean {
        return storage.update(t)
    }

    override fun delete(id: Long): Boolean {
        return storage.delete(id)
    }
}