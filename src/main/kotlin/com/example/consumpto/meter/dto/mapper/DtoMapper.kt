package com.example.consumpto.meter.dto.mapper

import kotlin.reflect.KType
import kotlin.reflect.typeOf

abstract class DtoMapper<T: Any, R: Any> {
    abstract val typeSignature: TypeSignature

    abstract fun map(from: T): R

    class TypeSignature(val from: KType, val to: KType) {
        companion object {
            inline operator fun <reified T: Any, reified R: Any> invoke() = TypeSignature(typeOf<T>(), typeOf<R>())
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TypeSignature

            if (from != other.from) return false
            if (to != other.to) return false

            return true
        }

        override fun hashCode(): Int {
            var result = from.hashCode()
            result = 31 * result + to.hashCode()
            return result
        }

        override fun toString(): String {
            return "TypeSignature(from=$from, to=$to)"
        }
    }
}
