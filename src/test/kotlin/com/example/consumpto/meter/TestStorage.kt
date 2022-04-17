package com.example.consumpto.meter

import com.example.consumpto.meter.entities.Entity

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

    fun update(t: T, params: List<Pair<String, Any>>) {
        TODO("Not yet implemented")
    }

    fun delete(t: T) {
        storage.values.remove(t)
    }
}