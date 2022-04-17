package com.example.consumpto.meter.dao

import com.example.consumpto.meter.entities.FuelRefill
import com.example.consumpto.meter.entities.FuelType
import java.math.BigDecimal
import java.sql.ResultSet
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Component

@Component
class FuelRefillH2Dao(private val jdbcTemplate: JdbcTemplate) : FuelRefillDao() {
    private val refillInsert = SimpleJdbcInsert(jdbcTemplate)
        .withTableName("refills")
        .usingGeneratedKeyColumns("id")

    init {
        executeScript()
    }

    private fun executeScript() {
        jdbcTemplate.update(
            "CREATE TABLE refills (" +
                    "   id IDENTITY, \n" +
                    "   fuel_type VARCHAR(4) NOT NULL, \n" +
                    "   price_per_liter NUMERIC(20, 2) NOT NULL, \n" +
                    "   amount NUMERIC(20, 2) NOT NULL, \n" +
                    "   driver_id BIGINT NOT NULL, \n" +
                    "   date DATE" +
                    ");")

    }

    override fun getAllRefillsSorted(driverId: Long?): List<FuelRefill> {
        return jdbcTemplate.query(
            "SELECT * FROM refills " +
                    if (driverId != null) "WHERE driver_id = $driverId" else "" +
                    "ORDER BY date;",
            RefillRowMapper()
        )
    }

    override fun getAll(): List<FuelRefill> {
        return jdbcTemplate.query("SELECT * FROM refills;", RefillRowMapper())
    }

    override fun get(id: Long): FuelRefill? {
        return jdbcTemplate.queryForObject("SELECT * FROM refills WHERE id = ?;", RefillRowMapper(), id)
    }

    override fun add(t: FuelRefill): Long {
        val parameters = mapOf<String, Any>(
            "fuel_type" to  t.fuelType.name,
            "price_per_liter" to  t.pricePerLiter,
            "amount" to  t.amount,
            "driver_id" to t.driverId,
            "date" to t.date
        )

        return refillInsert.executeAndReturnKey(parameters).toLong()
    }

    override fun update(t: FuelRefill, params: List<Pair<String, Any>>) {
        TODO("Not yet implemented")
    }

    override fun delete(t: FuelRefill) {
        TODO("Not yet implemented")
    }

    override fun addAll(collection: Collection<FuelRefill>): List<Long> {
        // TODO Change to batch
        return collection.map{ add(it) }
    }
}

class RefillRowMapper : RowMapper<FuelRefill> {
    override fun mapRow(rs: ResultSet, rowNum: Int): FuelRefill {
        val refill = FuelRefill(
            fuelType = FuelType.valueOf(rs.getString("fuel_type")),
            pricePerLiter = BigDecimal(rs.getString("price_per_liter")),
            amount = BigDecimal(rs.getString("amount")),
            driverId = rs.getLong("driver_id"),
            date = rs.getDate("date").toLocalDate()
        )

        refill.id = rs.getLong("id")

        return refill
    }
}
