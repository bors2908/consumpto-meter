package com.example.consumpto.meter

import com.example.consumpto.meter.entities.Entity
import java.security.InvalidParameterException

class TestStorage<T : Entity> {
    private val storage = mutableMapOf<Long, T>()
    private var lastId = 0L

    fun getAll(): List<T> {
        return storage.values.toList()
    }

    fun get(id: Long): T? {
        return storage[id]
    }

    fun add(t: T): Long {
        val id = lastId++

        t.id = id

        storage[id] = t

        return id
    }

    fun addAll(collection: Collection<T>): List<Long> {
        return collection.map { add(it) }
    }

    fun update(t: T): Boolean {
        storage[t.id ?: throw InvalidParameterException("Id should not be null")] = t

        return true
    }

    fun delete(id: Long): Boolean {
        return storage.values.remove(get(id))
    }
}