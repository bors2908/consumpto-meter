package com.example.consumpto.meter.dao

import com.example.consumpto.meter.entities.Entity

interface Dao<T: Entity> {
    fun getAll(): Collection<T>

    fun get(id: Long): T?

    fun add(t: T): Long

    fun addAll(collection: Collection<T>): List<Long>

    fun update(t: T): Boolean

    fun delete(id: Long): Boolean
}
