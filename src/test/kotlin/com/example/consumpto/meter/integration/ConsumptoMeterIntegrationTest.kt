package com.example.consumpto.meter.integration

import com.example.consumpto.meter.currencyScale
import com.example.consumpto.meter.dao.FuelRefillDao
import com.example.consumpto.meter.dto.AddRefillDto
import com.example.consumpto.meter.dto.RefillDto
import com.example.consumpto.meter.dto.StatDto
import com.example.consumpto.meter.getRandomDriver
import com.example.consumpto.meter.getRandomFuelAmount
import com.example.consumpto.meter.getRandomFuelPrice
import com.example.consumpto.meter.getRandomFuelType
import com.example.consumpto.meter.getRandomLocalDate
import com.example.consumpto.meter.rest.ADD_REFILLS_ENDPOINT
import com.example.consumpto.meter.rest.CONSUMPTION_URL_PATH
import com.example.consumpto.meter.rest.MONTHLY_AMOUNT_ENDPOINT
import com.example.consumpto.meter.rest.MONTHLY_REFILLS_ENDPOINT
import com.example.consumpto.meter.rest.MONTHLY_STATS_ENDPOINT
import com.example.consumpto.meter.toYearMonthLocalizedString
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.time.YearMonth
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class ConsumptoMeterIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: ObjectMapper

    @Autowired
    private lateinit var refillDao: FuelRefillDao

    @BeforeEach
    fun setUp() {
        refillDao.deleteAll()
    }

    @Test
    fun testIntegration() {
        checkIntegration(100)
    }

    @Test
    fun testIntegrationDriverId() {
        checkIntegration(100, getRandomDriver())
    }

    fun checkIntegration(count: Int, driverId: Long? = null) {
        val testAddDtos = generateAddRefillTestDtos(count)

        val filteredTestData = testAddDtos.filter { driverId == null || it.driverId == driverId }

        val sum = filteredTestData
            .map { (it.pricePerLiter * it.amount).currencyScale() }
            .reduce { acc, subSum -> acc + subSum }

        val months = filteredTestData
            .map { YearMonth.of(it.date.year, it.date.month).toYearMonthLocalizedString() }
            .distinct()

        val ids = addRefills(testAddDtos)

        assertEquals(ids.size, count)

        val costByMonth = getCostByMonth(driverId).mapValues { it.value }

        assertTrue(costByMonth.keys.containsAll(months))

        val resultingSum = costByMonth.values.reduce { acc, subSum -> acc + subSum }

        assertEquals(sum, resultingSum)

        val statsByMonth = getStatsByMonth(driverId)

        val resultingStatSum = statsByMonth.values
            .flatten()
            .map { it.totalPrice }
            .reduce { acc, subSum -> acc + subSum }

        assertEquals(sum, resultingStatSum)

        val refillsByMonth = getRefillsByMonth(driverId)

        val resultingRefills = refillsByMonth.values.flatten()

        assertTrue(resultingRefills.size == filteredTestData.size)
    }

    private fun addRefills(refills: List<AddRefillDto>): List<Long> {
        return sendRequest(HttpMethod.POST, CONSUMPTION_URL_PATH + ADD_REFILLS_ENDPOINT, refills)
    }

    private fun getCostByMonth(driverId: Long?): Map<String, BigDecimal> {
        return sendRequest(HttpMethod.GET, CONSUMPTION_URL_PATH + MONTHLY_AMOUNT_ENDPOINT, driverId ?: "")
    }

    private fun getStatsByMonth(driverId: Long?): Map<String, List<StatDto>> {
        return sendRequest(HttpMethod.GET, CONSUMPTION_URL_PATH + MONTHLY_STATS_ENDPOINT, driverId ?: "")
    }

    private fun getRefillsByMonth(driverId: Long?): Map<String, List<RefillDto>> {
        return sendRequest(HttpMethod.GET, CONSUMPTION_URL_PATH + MONTHLY_REFILLS_ENDPOINT, driverId ?: "")
    }

    private inline fun <reified R, reified T> sendRequest(httpMethod: HttpMethod, path: String, requestData: R): T {
        val requestBody = jsonMapper.writeValueAsString(requestData)

        val responseBody = mockMvc
            .perform(
                when (httpMethod) {
                    HttpMethod.POST -> MockMvcRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                    HttpMethod.GET -> MockMvcRequestBuilders.get(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                    else -> throw UnsupportedOperationException("Unsupported HTTP method: $httpMethod")
                }
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
            .response
            .getContentAsString(Charsets.UTF_8)

        return jsonMapper.readValue(responseBody, object : TypeReference<T>() {})
    }

    private fun generateAddRefillTestDtos(count: Int): List<AddRefillDto> = (1..count)
        .map {
            AddRefillDto(
                getRandomFuelType(),
                getRandomFuelPrice(),
                getRandomFuelAmount(),
                getRandomLocalDate(),
                getRandomDriver()
            )
        }
}
