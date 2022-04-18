package com.example.consumpto.meter.rest

import com.example.consumpto.meter.ConsumptoMeterService
import com.example.consumpto.meter.dto.AddRefillDTO
import com.example.consumpto.meter.dto.DtoMapper
import com.example.consumpto.meter.dto.RefillDTO
import com.example.consumpto.meter.dto.StatDTO
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.Positive
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/consumption")
class ConsumptionRestController(
    private val service: ConsumptoMeterService,
    private val mapper: DtoMapper,
) {
    @PostMapping(path = ["/addRefills"])
    @ResponseBody
    fun addRefills(@RequestBody @Valid addRefills: List<@Valid AddRefillDTO>): List<Long> {
        return service.addRefills(addRefills.map { mapper.addRefillDtoToRefill(it) })
    }

    @GetMapping(path = ["/monthlyAmount"])
    @ResponseBody
    fun getMonthlyAmount(@RequestBody @Valid @Positive driverId: Long? = null): Map<String, BigDecimal> {
        return mapper.mapMonthKeys(service.getCostByMonth(driverId))
    }

    @GetMapping(path = ["/monthlyRefills"])
    @ResponseBody
    fun getMonthlyRefills(@RequestBody @Valid @Positive driverId: Long? = null): Map<String, List<RefillDTO>> {
        return mapper.mapRefills(service.getRefillsByMonth(driverId))
    }

    @GetMapping(path = ["/monthlyStats"])
    @ResponseBody
    fun getMonthlyStats(@RequestBody @Valid @Positive driverId: Long? = null): Map<String, List<StatDTO>> {
        return mapper.mapStats(service.getStatsByMonth(driverId))
    }
}
