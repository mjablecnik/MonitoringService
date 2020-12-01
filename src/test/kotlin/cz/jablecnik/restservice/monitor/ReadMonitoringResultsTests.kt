package cz.jablecnik.restservice.monitor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import cz.jablecnik.restservice.APPLICATION_JSON_UTF8
import cz.jablecnik.restservice.TestConfig
import cz.jablecnik.restservice.user.User
import cz.jablecnik.restservice.user.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.annotation.PostConstruct



@ContextConfiguration(classes = [TestConfig::class])
@AutoConfigureMockMvc
@SpringBootTest
open class ReadMonitoringResultsTests {

    val firstUser = User(id = 10, name = "Martin", email = "martin@example.com", passwordHash = "\$2a\$10\$OYYkXEap4nbGclMTVwdyqeCX5czN83Gcil4SXDwgIEoIsLdk.aZSW")
    val secondUser = User(id = 20, name = "Michal", email = "michal@example.com", passwordHash = "\$2a\$10\$0fJ6PjtR/85D35MBWkI3A.xibKZJ6iaa/ZggJAIEHwQs4P83URjuG")

    @Autowired
    private var mockMvc: MockMvc? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val monitoredEndpointRepository: MonitoredEndpointRepository? = null

    @Autowired
    private val monitoringResultRepository: MonitoringResultRepository? = null

    @PostConstruct
    open fun setup() {

        Mockito.`when`(userRepository!!.findByEmail(firstUser.email!!)).thenReturn(firstUser)
        Mockito.`when`(userRepository.findByEmail(secondUser.email!!)).thenReturn(secondUser)

        val firstMonitoredEndpoint = MonitoredEndpoint(
                id = 1,
                name = "Test name",
                url = "http://test.example.com",
                monitoredInterval = 60,
                created = LocalDateTime.now(),
                lastCheck = LocalDateTime.now(),
                owner = firstUser
        )

        Mockito.`when`(monitoredEndpointRepository!!.findById(1)).thenReturn(firstMonitoredEndpoint)

        Mockito.`when`(monitoringResultRepository!!.findResults(1, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"))))
                .thenReturn(monitoringResultsGenerator(5, firstMonitoredEndpoint))
    }

    private fun monitoringResultsGenerator(number: Int, endpoint: MonitoredEndpoint, withEndpoint: Boolean = true): List<MonitoringResult> {
        val results = mutableListOf<MonitoringResult>()
        for (i in 1..number) {
            val id = (endpoint.id!! * i).toInt()
            results.add(MonitoringResult(
                    id = id.toLong(),
                    dateOfCheck = LocalDateTime.ofEpochSecond((LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)-1000L) + (endpoint.monitoredInterval!!*i).toLong(), 0, ZoneOffset.UTC),
                    returnedHttpStatusCode = 200,
                    returnedPayload = "Test pyload.",
                    monitoredEndpoint = if (withEndpoint) endpoint else null
            ))
        }
        return results
    }

    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `read list of monitored endpoint results`() {
        val request = MockMvcRequestBuilders
                .get("/monitor/endpoint/1/result?page=1&size=5")
                .contentType(APPLICATION_JSON_UTF8)

        val response = mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.results").exists())
                .andReturn()

        val responseEndpointsData = jacksonObjectMapper().readValue<Map<Any, Any>>(response.response.contentAsString)["results"] as List<Map<String, Any>>

        Assertions.assertEquals(5, responseEndpointsData.size)
        for (i in 0..4) {
            Assertions.assertEquals(listOf("id", "dateOfCheck", "returnedHttpStatusCode", "returnedPayload"), responseEndpointsData[i].keys.toList())
        }
    }

    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `read list of monitored endpoint results with wrong params`() {
        val wrongParams = listOf("page=0", "page=-1", "size=-5", "size=0", "sort=asdf")

        for (wrongParam in wrongParams) {
            val request = MockMvcRequestBuilders
                    .get("/monitor/endpoint/1/result?$wrongParam")
                    .contentType(APPLICATION_JSON_UTF8)

            mockMvc!!.perform(request)
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }
    }

    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `read list of monitored endpoint results with not existing id`() {
        val request = MockMvcRequestBuilders
                .get("/monitor/endpoint/111/result")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @WithUserDetails("michal@example.com", userDetailsServiceBeanName = "authService")
    fun `read list of monitored endpoint results with different owner`() {
        val request = MockMvcRequestBuilders
                .get("/monitor/endpoint/1/result?page=1&size=5")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden)
                .andReturn()
    }

    @Test
    fun `read list of monitored endpoint results with wrong token`() {
        val request = MockMvcRequestBuilders
                .get("/monitor/endpoint/1/result")
                .header("Authorization", "Bearer: wrongToken")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `read list of monitored endpoint results without token`() {
        val request = MockMvcRequestBuilders
                .get("/monitor/endpoint/1/result")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }
}