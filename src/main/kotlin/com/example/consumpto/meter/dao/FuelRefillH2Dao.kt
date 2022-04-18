package com.example.consumpto.meter.dao

import com.example.consumpto.meter.domain.FuelRefill
import com.example.consumpto.meter.domain.FuelType
import java.math.BigDecimal
import java.sql.ResultSet
import java.sql.SQLDataException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Component

@Component
class FuelRefillH2Dao(private val jdbcTemplate: JdbcTemplate) : FuelRefillDao() {
    companion object {
        const val REFILLS_TABLE_NAME = "refills"
        const val ID_COLUMN_NAME = "id"
        const val FUEL_TYPE_COLUMN_NAME = "fuel_type"
        const val PRICE_PER_LITER_COLUMN_NAME = "price_per_liter"
        const val AMOUNT_COLUMN_NAME = "amount"
        const val DRIVER_ID_COLUMN_NAME = "driver_id"
        const val DATE_COLUMN_NAME = "date"

        // I thought about separating the table with fuel names, prices and respective dates to reduce data redundancy,
        // but that would require separating fuel pricing input to another endpoint (which contradicts with app
        // specification) or addition of ineffective fuel types parsing from DB / memory caching each time a refill
        // is added.
        private const val CREATE_SQL_SCRIPT = "CREATE TABLE $REFILLS_TABLE_NAME (" +
                "   $ID_COLUMN_NAME IDENTITY, \n" +
                "   $FUEL_TYPE_COLUMN_NAME VARCHAR(4) NOT NULL, \n" +
                "   $PRICE_PER_LITER_COLUMN_NAME NUMERIC(20, 2) NOT NULL, \n" +
                "   $AMOUNT_COLUMN_NAME NUMERIC(20, 2) NOT NULL, \n" +
                "   $DRIVER_ID_COLUMN_NAME BIGINT NOT NULL, \n" +
                "   $DATE_COLUMN_NAME DATE" +
                ");"
    }

    private val refillInsert = SimpleJdbcInsert(jdbcTemplate)
        .withTableName(REFILLS_TABLE_NAME)
        .usingGeneratedKeyColumns(ID_COLUMN_NAME)

    init {
        jdbcTemplate.update(CREATE_SQL_SCRIPT)
    }

    override fun getAllRefillsSorted(driverId: Long?): List<FuelRefill> {
        return jdbcTemplate.query(
            "SELECT * " +
                    "FROM $REFILLS_TABLE_NAME " +
                    if (driverId != null) "WHERE $DRIVER_ID_COLUMN_NAME = $driverId " else "" +
                    "ORDER BY $DATE_COLUMN_NAME;",
            RefillRowMapper()
        )
    }

    override fun getAll(): List<FuelRefill> {
        return jdbcTemplate.query("SELECT * FROM $REFILLS_TABLE_NAME;", RefillRowMapper())
    }

    override fun get(id: Long): FuelRefill? {
        return jdbcTemplate.queryForObject(
            "SELECT * " +
                    "FROM $REFILLS_TABLE_NAME " +
                    "WHERE $ID_COLUMN_NAME = ?;",
            RefillRowMapper(),
            id
        )
    }

    override fun add(t: FuelRefill): Long {
        return refillInsert.executeAndReturnKey(t.toParametersMap()).toLong()
    }

    override fun update(t: FuelRefill): Boolean {
        val updated = jdbcTemplate.update(
            "UPDATE $REFILLS_TABLE_NAME " +
                    "SET " +
                    "$FUEL_TYPE_COLUMN_NAME = ?, " +
                    "$PRICE_PER_LITER_COLUMN_NAME = ?, " +
                    "$AMOUNT_COLUMN_NAME  = ?, " +
                    "$DRIVER_ID_COLUMN_NAME  = ?, " +
                    "$DATE_COLUMN_NAME  = ?, " +
                    "WHERE $ID_COLUMN_NAME = ?;",
            t.fuelType,
            t.pricePerLiter,
            t.amount,
            t.driverId,
            t.date,
            t.id
        )

        return processSingleUpdateResult(updated, "$updated rows were updated instead of one.")
    }

    override fun delete(id: Long): Boolean {
        val deleted = jdbcTemplate.update(
            "DELETE " +
                    "FROM $REFILLS_TABLE_NAME " +
                    "WHERE $ID_COLUMN_NAME = ?;",
            id
        )

        return processSingleUpdateResult(deleted, "$deleted rows were deleted instead of one.")
    }

    override fun deleteAll() {
        jdbcTemplate.update("TRUNCATE TABLE $REFILLS_TABLE_NAME;")
    }

    private fun processSingleUpdateResult(updated: Int, message: String): Boolean {
        return when (updated) {
            0 -> false
            1 -> true
            // Most likely unreachable.
            else -> throw SQLDataException(message)
        }
    }

    override fun addAll(collection: Collection<FuelRefill>): List<Long> {
        // Batch update did not allow getting generated keys.
        return collection.map { add(it) }
    }

    private fun FuelRefill.toParametersMap() = mapOf<String, Any>(
        FUEL_TYPE_COLUMN_NAME to this.fuelType.name,
        PRICE_PER_LITER_COLUMN_NAME to this.pricePerLiter,
        AMOUNT_COLUMN_NAME to this.amount,
        DRIVER_ID_COLUMN_NAME to this.driverId,
        DATE_COLUMN_NAME to this.date
    )

    class RefillRowMapper : RowMapper<FuelRefill> {
        override fun mapRow(rs: ResultSet, rowNum: Int): FuelRefill {
            val refill = FuelRefill(
                fuelType = FuelType.valueOf(rs.getString(FUEL_TYPE_COLUMN_NAME)),
                pricePerLiter = BigDecimal(rs.getString(PRICE_PER_LITER_COLUMN_NAME)),
                amount = BigDecimal(rs.getString(AMOUNT_COLUMN_NAME)),
                driverId = rs.getLong(DRIVER_ID_COLUMN_NAME),
                date = rs.getDate(DATE_COLUMN_NAME).toLocalDate()
            )

            refill.id = rs.getLong(ID_COLUMN_NAME)

            return refill
        }
    }
}


