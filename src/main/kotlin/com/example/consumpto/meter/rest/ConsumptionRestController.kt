package com.example.consumpto.meter.rest

import com.example.consumpto.meter.ConsumptoMeterService
import com.example.consumpto.meter.dto.AddRefillDto
import com.example.consumpto.meter.dto.DtoMapper
import com.example.consumpto.meter.dto.RefillDto
import com.example.consumpto.meter.dto.StatDto
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Positive
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

const val CONSUMPTION_URL_PATH = "/consumption"
const val ADD_REFILLS_ENDPOINT = "/addRefills"
const val MONTHLY_AMOUNT_ENDPOINT = "/monthlyAmount"
const val MONTHLY_REFILLS_ENDPOINT = "/monthlyRefills"
const val MONTHLY_STATS_ENDPOINT = "/monthlyStats"

@Validated
@RestController
@RequestMapping(CONSUMPTION_URL_PATH)
class ConsumptionRestController(
    private val service: ConsumptoMeterService,
    private val mapper: DtoMapper,
) {
    @PostMapping(path = [ADD_REFILLS_ENDPOINT])
    @ResponseBody
    fun addRefills(@RequestBody @Valid @NotEmpty addRefills: List<@Valid AddRefillDto>): List<Long> {
        return service.addRefills(addRefills.map { mapper.addRefillDtoToRefill(it) })
    }

    @GetMapping(path = [MONTHLY_AMOUNT_ENDPOINT])
    @ResponseBody
    fun getMonthlyAmount(@RequestBody @Valid @Positive driverId: Long? = null): Map<String, BigDecimal> {
        return mapper.mapMonthKeys(service.getCostByMonth(driverId))
    }

    @GetMapping(path = [MONTHLY_REFILLS_ENDPOINT])
    @ResponseBody
    fun getMonthlyRefills(@RequestBody @Valid @Positive driverId: Long? = null): Map<String, List<RefillDto>> {
        return mapper.mapRefills(service.getRefillsByMonth(driverId))
    }

    @GetMapping(path = [MONTHLY_STATS_ENDPOINT])
    @ResponseBody
    fun getMonthlyStats(@RequestBody @Valid @Positive driverId: Long? = null): Map<String, List<StatDto>> {
        return mapper.mapStats(service.getStatsByMonth(driverId))
    }
}
