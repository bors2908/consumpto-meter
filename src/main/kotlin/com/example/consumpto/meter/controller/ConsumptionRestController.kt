package com.example.consumpto.meter.controller

import com.example.consumpto.meter.ConsumptoMeterService
import com.example.consumpto.meter.dto.AddRefillDTO
import com.example.consumpto.meter.dto.DtoMapper
import com.example.consumpto.meter.dto.RefillDTO
import com.example.consumpto.meter.dto.StatDTO
import java.math.BigDecimal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/consumption")
class ConsumptionRestController(
    private val service: ConsumptoMeterService,
    private val mapper: DtoMapper
) {
    @PostMapping(path = ["/addRefill"])
    @ResponseBody
    fun addRefill(@RequestBody addRefill: AddRefillDTO): Long {
        return service.addRefill(mapper.addRefillDtoToRefill(addRefill))
    }

    @PostMapping(path = ["/addRefills"])
    @ResponseBody
    fun addRefills(@RequestBody addRefills: List<AddRefillDTO>): List<Long> {
        return service.addRefills(addRefills.map { mapper.addRefillDtoToRefill(it) })
    }

    @GetMapping(path = ["/monthlyAmount"])
    @ResponseBody
    fun getMonthlyAmount(@RequestBody driverId: Long? = null): Map<String, BigDecimal> {
        return service.getCostByMonth(driverId)
    }

    @GetMapping(path = ["/monthlyRefills"])
    @ResponseBody
    fun getMonthlyRefills(@RequestBody driverId: Long? = null): Map<String, List<RefillDTO>> {
        return service.getRefillsByMonth(driverId)
            .mapValues { it.value.map { e -> mapper.refillToDto(e) } }
    }

    @GetMapping(path = ["/monthlyStats"])
    @ResponseBody
    fun getMonthlyStats(@RequestBody driverId: Long? = null): Map<String, List<StatDTO>> {
        return service.getStatsByMonth(driverId).mapValues { mapper.statToDto(it.value) }
    }
}
