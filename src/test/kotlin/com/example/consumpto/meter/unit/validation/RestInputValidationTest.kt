package com.example.consumpto.meter.unit.validation

import com.example.consumpto.meter.service.StatisticService
import com.example.consumpto.meter.domain.FuelType
import com.example.consumpto.meter.dto.DtoMapper
import com.example.consumpto.meter.getRandomDriver
import com.example.consumpto.meter.getRandomFuelAmount
import com.example.consumpto.meter.getRandomFuelPrice
import com.example.consumpto.meter.getRandomFuelType
import com.example.consumpto.meter.getRandomLocalDate
import com.example.consumpto.meter.getRandomizedRefill
import com.example.consumpto.meter.rest.ADD_REFILLS_ENDPOINT
import com.example.consumpto.meter.rest.CONSUMPTION_URL_PATH
import com.example.consumpto.meter.rest.ConsumptionRestController
import com.example.consumpto.meter.rest.MONTHLY_AMOUNT_ENDPOINT
import com.example.consumpto.meter.rest.MONTHLY_REFILLS_ENDPOINT
import com.example.consumpto.meter.rest.MONTHLY_STATS_ENDPOINT
import com.example.consumpto.meter.service.NewRefillService
import java.math.BigDecimal
import java.time.LocalDate
import org.hamcrest.Matchers.containsString
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(ConsumptionRestController::class)
class RestInputValidationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var mapper: DtoMapper

    @MockBean
    private lateinit var statisticService: StatisticService

    @MockBean
    private lateinit var newRefillService: NewRefillService

    @BeforeAll
    fun initMocks() {
        whenever(newRefillService.addRefills(anyOrNull())).doReturn(emptyList())
        whenever(statisticService.getCostByMonth()).doReturn(emptyMap())
        whenever(statisticService.getRefillsByMonth()).doReturn(emptyMap())
        whenever(statisticService.getStatsByMonth()).doReturn(emptyMap())

        whenever(mapper.addRefillDtoToRefill(anyOrNull())).doReturn(getRandomizedRefill())
        whenever(mapper.mapMonthKeys<Any>(anyOrNull())).doReturn(emptyMap())
        whenever(mapper.mapRefills(anyOrNull())).doReturn(emptyMap())
        whenever(mapper.mapStats(anyOrNull())).doReturn(emptyMap())
    }

    @Test
    fun testValidData() {
        val requestData = getJsonRequestData()

        mockMvc
            .perform(
                post(CONSUMPTION_URL_PATH + ADD_REFILLS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData.toString())
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun testWrongPriceFormat() {
        val requestData = getJsonRequestData()

        requestData.put(getRequestJsonObject(pricePerLiter = getRandomFuelPrice().setScale(3)))

        mockMvc
            .perform(
                post(CONSUMPTION_URL_PATH + ADD_REFILLS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData.toString())
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(containsString("numeric value out of bounds")))
    }

    @Test
    fun testNotParseable() {
        val requestData = JSONArray()

        requestData.put("crap")

        mockMvc
            .perform(
                post(CONSUMPTION_URL_PATH + ADD_REFILLS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData.toString())
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(containsString("Illegal input")))
    }

    @Test
    fun testNotParseableAfterParseable() {
        val requestData = getJsonRequestData()

        requestData.put(JSONObject(mapOf("fuelType" to "98")))

        mockMvc
            .perform(
                post(CONSUMPTION_URL_PATH + ADD_REFILLS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData.toString())
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(containsString("Illegal input")))
    }

    @Test
    fun testEmptyArray() {
        val requestData = JSONArray()

        mockMvc
            .perform(
                post(CONSUMPTION_URL_PATH + ADD_REFILLS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData.toString())
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(containsString("must not be empty")))
    }

    @Test
    fun testNegativeAmount() {
        val requestData = getJsonRequestData()

        requestData.put(getRequestJsonObject(amount = getRandomFuelAmount().negate()))

        mockMvc
            .perform(
                post(CONSUMPTION_URL_PATH + ADD_REFILLS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData.toString())
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(containsString("Fuel amount must be positive")))
    }

    @Test
    fun testPriceWrongFormat() {
        val requestData = getJsonRequestData()

        requestData.put(getRequestJsonObject(pricePerLiter = getRandomFuelPrice().setScale(3)))

        mockMvc
            .perform(
                post(CONSUMPTION_URL_PATH + ADD_REFILLS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData.toString())
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(containsString("numeric value out of bounds")))
    }

    @Test
    fun testPriceZeroDriverID() {
        val requestData = getJsonRequestData()

        requestData.put(getRequestJsonObject(driverId = 0))

        mockMvc
            .perform(
                post(CONSUMPTION_URL_PATH + ADD_REFILLS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestData.toString())
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(containsString("must be greater than 0")))
    }

    @Test
    fun testGetRequestsWithWrongDriverId() {
        checkGetEndpointDriverId(MONTHLY_AMOUNT_ENDPOINT)
        checkGetEndpointDriverId(MONTHLY_REFILLS_ENDPOINT)
        checkGetEndpointDriverId(MONTHLY_STATS_ENDPOINT)
    }

    private fun checkGetEndpointDriverId(endpoint: String) {
        mockMvc
            .perform(
                get(CONSUMPTION_URL_PATH + endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("-5")
            )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(containsString("must be greater than 0")))
    }

    private fun getJsonRequestData(): JSONArray {
        val requestData = JSONArray((0..10).map {
            getRequestJsonObject()
        })
        return requestData
    }

    // Intentional avoidance of Jackson DTO mapping.
    private fun getRequestJsonObject(
        fuelType: FuelType = getRandomFuelType(),
        pricePerLiter: BigDecimal = getRandomFuelPrice(),
        amount: BigDecimal = getRandomFuelAmount(),
        date: LocalDate = getRandomLocalDate(),
        driverId: Long = getRandomDriver(),
    ) = JSONObject(mapOf(
        "fuelType" to fuelType.type,
        "pricePerLiter" to pricePerLiter,
        "amount" to amount,
        "driverId" to driverId,
        "date" to date
    ))
}
