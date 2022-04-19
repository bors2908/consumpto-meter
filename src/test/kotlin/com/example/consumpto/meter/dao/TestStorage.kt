package com.example.consumpto.meter.dao

import com.example.consumpto.meter.domain.Entity
import java.security.InvalidParameterException
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class TestStorage<T : Entity> {
    private var storage = mutableMapOf<Long, T>()
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

    fun deleteAll() {
        log.warn { "Clearing storage." }

        storage = mutableMapOf()
    }
}