package com.example.consumpto.meter.dao

import com.example.consumpto.meter.domain.Entity
import javax.validation.Valid
import org.springframework.validation.annotation.Validated

@Validated
interface Dao<T: Entity> {
    @Valid
    fun getAll(): Collection<@Valid T>

    @Valid
    fun get(id: Long): T?

    fun add(@Valid t: T): Long

    fun addAll(@Valid collection: Collection<@Valid T>): List<Long>

    fun update(@Valid t: T): Boolean

    fun delete(id: Long): Boolean

    fun deleteAll()
}
