package com.example.consumpto.meter.dto.mapper

import org.springframework.stereotype.Component

@Component
final class DtoMapperResolver(mappers: List<DtoMapper<out Any, out Any>>) {
    val dtoMappersMap = mappers.associateBy { it.typeSignature }

    inline fun <reified T : Any, reified R : Any> map(from: T): R {
        val typeSignature = DtoMapper.TypeSignature<T, R>()

        val uncastedMapper = dtoMappersMap
            .getOrElse(typeSignature) {
                throw UnsupportedOperationException("No mapper for types $typeSignature available.")
            }

        try {
            @Suppress("UNCHECKED_CAST")
            return (uncastedMapper as DtoMapper<T, R>).map(from)
        } catch (e: ClassCastException) {
            throw UnsupportedOperationException(
                "Casting failure. Most likely signature type is misconfigured: ${uncastedMapper.typeSignature}",
                e
            )
        }
    }
}
