package cz.jablecnik.restservice.monitor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import cz.jablecnik.restservice.APPLICATION_JSON_UTF8
import cz.jablecnik.restservice.TestConfig
import cz.jablecnik.restservice.auth.JwtResponse
import cz.jablecnik.restservice.user.User
import cz.jablecnik.restservice.user.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime
import javax.annotation.PostConstruct



@ContextConfiguration(classes = [TestConfig::class])
@AutoConfigureMockMvc
@SpringBootTest
open class ReadMonitoredEndpointTests {

    val firstUser = User(id = 10, name = "Martin", email = "martin@example.com", passwordHash = "\$2a\$10\$OYYkXEap4nbGclMTVwdyqeCX5czN83Gcil4SXDwgIEoIsLdk.aZSW")
    val secondUser = User(id = 20, name = "Michal", email = "michal@example.com", passwordHash = "\$2a\$10\$0fJ6PjtR/85D35MBWkI3A.xibKZJ6iaa/ZggJAIEHwQs4P83URjuG")

    @Autowired
    private var mockMvc: MockMvc? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val monitoredEndpointRepository: MonitoredEndpointRepository? = null

    @PostConstruct
    open fun setup() {

        Mockito.`when`(userRepository!!.findByEmail(firstUser.email!!)).thenReturn(firstUser)
        Mockito.`when`(userRepository.findByEmail(secondUser.email!!)).thenReturn(secondUser)

        Mockito.`when`(monitoredEndpointRepository!!.findByOwner(10, PageRequest.of(0, 5)))
                .thenReturn(monitoredEndpointGenerator(5, firstUser))

        Mockito.`when`(monitoredEndpointRepository.findByOwner(10, PageRequest.of(0, 3)))
                .thenReturn(monitoredEndpointGenerator(3, firstUser))

        Mockito.`when`(monitoredEndpointRepository.findByOwner(20, PageRequest.of(0, 5)))
                .thenReturn(listOf())
    }

    private fun monitoredEndpointGenerator(number: Int, owner: User, withOwner: Boolean = true): List<MonitoredEndpoint> {
        val endpoints = mutableListOf<MonitoredEndpoint>()
        for (i in 1..number) {
            val id = (owner.id!! * i).toInt()
            endpoints.add(MonitoredEndpoint(
                    id = id.toLong(),
                    name = "Test name $id",
                    url = "http://test$id.example.com",
                    monitoredInterval = 60 + id*10,
                    created = LocalDateTime.now(),
                    lastCheck = LocalDateTime.now(),
                    owner = if (withOwner) owner else null
            ))
        }
        return endpoints
    }

    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `read list of monitored endpoints`() {
        val request = MockMvcRequestBuilders
                .get("/monitor/endpoint?page=1&size=5")
                .contentType(APPLICATION_JSON_UTF8)

        val response = mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.endpoints").exists())
                .andReturn()

        val responseEndpointsData = jacksonObjectMapper().readValue<Map<Any, Any>>(response.response.contentAsString)["endpoints"] as List<Map<String, Any>>

        Assertions.assertEquals(5, responseEndpointsData.size)
        for (i in 0..4) {
            Assertions.assertEquals(listOf("id", "name", "url", "created", "lastCheck", "monitoredInterval"), responseEndpointsData[i].keys.toList())
        }
    }

    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `read list of monitored endpoints with wrong params`() {
        val wrongParams = listOf("page=0", "page=-1", "size=-5", "size=0")

        for (wrongParam in wrongParams) {
            val request = MockMvcRequestBuilders
                    .get("/monitor/endpoint?$wrongParam")
                    .contentType(APPLICATION_JSON_UTF8)

            mockMvc!!.perform(request)
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }
    }

    @Test
    @WithUserDetails("michal@example.com", userDetailsServiceBeanName = "authService")
    fun `read list of monitored endpoints of owner`() {
        val request = MockMvcRequestBuilders
                .get("/monitor/endpoint?page=1&size=5")
                .contentType(APPLICATION_JSON_UTF8)

        val response = mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.endpoints").exists())
                .andReturn()

        val responseEndpointsData = jacksonObjectMapper().readValue<Map<Any, Any>>(response.response.contentAsString)["endpoints"] as List<Map<String, Any>>

        Assertions.assertEquals(0, responseEndpointsData.size)
    }

    @Test
    fun `read list monitored endpoints with wrong token`() {
        val request = MockMvcRequestBuilders
                .get("/monitor/endpoint")
                .header("Authorization", "Bearer: wrongToken")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `read list monitored endpoints without token`() {
        val request = MockMvcRequestBuilders
                .get("/monitor/endpoint")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }
}