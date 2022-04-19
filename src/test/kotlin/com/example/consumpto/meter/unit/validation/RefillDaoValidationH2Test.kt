package com.example.consumpto.meter.unit.validation

import com.example.consumpto.meter.dao.FuelRefillH2Dao
import com.example.consumpto.meter.dao.FuelRefillH2Dao.Companion.AMOUNT_COLUMN_NAME
import com.example.consumpto.meter.dao.FuelRefillH2Dao.Companion.ID_COLUMN_NAME
import com.example.consumpto.meter.dao.FuelRefillH2Dao.Companion.PRICE_PER_LITER_COLUMN_NAME
import com.example.consumpto.meter.dao.FuelRefillH2Dao.Companion.REFILLS_TABLE_NAME
import com.example.consumpto.meter.domain.FuelRefill
import com.example.consumpto.meter.getRandomDriver
import com.example.consumpto.meter.getRandomFuelAmount
import com.example.consumpto.meter.getRandomFuelPrice
import com.example.consumpto.meter.getRandomFuelType
import com.example.consumpto.meter.getRandomLocalDate
import java.math.BigDecimal
import javax.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class RefillDaoValidationH2Test : RefillDaoValidationAbstractTest<FuelRefillH2Dao>() {
    @Autowired
    override lateinit var fuelRefillDao: FuelRefillH2Dao

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun testBrokenDbData() {
        fuelRefillDao.addAll(listOf(
            FuelRefill(
                getRandomFuelType(),
                getRandomFuelPrice(),
                getRandomFuelAmount(),
                getRandomDriver(),
                getRandomLocalDate()
            )
        ))

        breakDbData()

        assertThrows<ConstraintViolationException> { fuelRefillDao.getAll() }
    }

    @Test
    fun testBrokenDbDataSortedRequest() {
        fuelRefillDao.addAll(listOf(
            FuelRefill(
                getRandomFuelType(),
                getRandomFuelPrice(),
                getRandomFuelAmount(),
                getRandomDriver(),
                getRandomLocalDate()
            )
        ))

        breakDbData()

        assertThrows<ConstraintViolationException> { fuelRefillDao.getAllRefillsSorted() }
    }

    private fun breakDbData() {
        jdbcTemplate.update(
            "UPDATE $REFILLS_TABLE_NAME " +
                    "SET " +
                    "$PRICE_PER_LITER_COLUMN_NAME = ?, " +
                    "$AMOUNT_COLUMN_NAME  = ? " +
                    "WHERE $ID_COLUMN_NAME > 0;",
            BigDecimal(-1.0),
            BigDecimal(-1.0)
        )
    }
}
