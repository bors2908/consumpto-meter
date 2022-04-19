package com.example.consumpto.meter.unit.validation

import com.example.consumpto.meter.TestFuelRefill
import com.example.consumpto.meter.dao.FuelRefillDao
import com.example.consumpto.meter.getRandomDriver
import com.example.consumpto.meter.getRandomFuelAmount
import com.example.consumpto.meter.getRandomFuelPrice
import com.example.consumpto.meter.getRandomFuelType
import com.example.consumpto.meter.getRandomLocalDate
import javax.validation.ConstraintViolationException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

abstract class RefillDaoValidationAbstractTest <T: FuelRefillDao> {
    abstract var fuelRefillDao: T

    @AfterEach
    fun tearDown() {
        fuelRefillDao.deleteAll()
    }

    @Test
    fun addNegativeDriverId() {
        assertThrows<ConstraintViolationException> {
            fuelRefillDao.addAll(listOf(
                TestFuelRefill(
                    getRandomFuelType(),
                    getRandomFuelPrice(),
                    getRandomFuelAmount(),
                    -getRandomDriver(),
                    getRandomLocalDate()
                )
            ))
        }
    }

    @Test
    fun addNegativePrice() {
        assertThrows<ConstraintViolationException> {
            fuelRefillDao.addAll(listOf(
                TestFuelRefill(
                    getRandomFuelType(),
                    getRandomFuelPrice().negate(),
                    getRandomFuelAmount(),
                    getRandomDriver(),
                    getRandomLocalDate()
                )
            ))
        }
    }

    @Test
    fun addWrongAmountFormat() {
        assertThrows<ConstraintViolationException> {
            fuelRefillDao.addAll(listOf(
                TestFuelRefill(
                    getRandomFuelType(),
                    getRandomFuelPrice(),
                    getRandomFuelAmount().setScale(3),
                    getRandomDriver(),
                    getRandomLocalDate()
                )
            ))
        }
    }
}