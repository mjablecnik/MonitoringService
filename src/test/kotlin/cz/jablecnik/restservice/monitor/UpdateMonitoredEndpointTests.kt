package cz.jablecnik.restservice.monitor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.jablecnik.restservice.APPLICATION_JSON_UTF8
import cz.jablecnik.restservice.TestConfig
import cz.jablecnik.restservice.user.User
import cz.jablecnik.restservice.user.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
open class UpdateMonitoredEndpointTests {

    @Autowired
    private var mockMvc: MockMvc? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val monitoredEndpointRepository: MonitoredEndpointRepository? = null

    @PostConstruct
    open fun setup() {
        val firstUser = User(id = 1, name = "Martin", email = "martin@example.com", passwordHash = "\$2a\$10\$OYYkXEap4nbGclMTVwdyqeCX5czN83Gcil4SXDwgIEoIsLdk.aZSW")
        val secondUser = User(id = 2, name = "Michal", email = "michal@example.com", passwordHash = "\$2a\$10\$0fJ6PjtR/85D35MBWkI3A.xibKZJ6iaa/ZggJAIEHwQs4P83URjuG")

        Mockito.`when`(monitoredEndpointRepository!!.findById(1))
                .thenReturn(MonitoredEndpoint(
                        id = 1,
                        name = "Test name",
                        url = "http://test.example.com",
                        monitoredInterval = 60,
                        created = LocalDateTime.now(),
                        lastCheck = LocalDateTime.now(),
                        owner = firstUser
                ))

        Mockito.`when`(userRepository!!.findByEmail(firstUser.email!!)).thenReturn(firstUser)
        Mockito.`when`(userRepository.findByEmail(secondUser.email!!)).thenReturn(secondUser)
    }


    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `update monitored endpoint`() {
        val requestDataList = listOf(
                MonitoredEndpointRequest(name = "Only http address", url = "http://www.seznam.cz/", interval = 5),
                MonitoredEndpointRequest(name = "Https address", url = "http://www.centrum.cz/", interval = 50),
                MonitoredEndpointRequest(name = "My ip address", url = "http://77.75.75.176", interval = 890),
                MonitoredEndpointRequest(name = "My ip address", url = "http://77.75.75.176:8080", interval = 890)
        )

        for (requestData in requestDataList) {
            val request = MockMvcRequestBuilders
                    .put("/monitor/endpoint/1")
                    .content(jacksonObjectMapper().writeValueAsString(requestData))
                    .contentType(APPLICATION_JSON_UTF8)

            mockMvc!!.perform(request)
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk)
        }
    }

    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `update monitored endpoint with wrong params`() {
        val requestDataList = listOf(
                mapOf("name" to "Only http address", "url" to "www.seznam.cz/", "interval" to 5),
                mapOf("name" to "Empty address", "url" to "", "interval" to 5),
                mapOf("name" to "Only http address", "url" to "77.75.75.176", "interval" to 5),
                mapOf("name" to "Not http address", "url" to "tcp://www.example.cz/", "interval" to 5),
                mapOf("name" to "With negative interval", "url" to "http://www.seznam.cz/", "interval" to -5),
                mapOf("name" to "With zero interval", "url" to "tcp://www.seznam.cz/", "interval" to 0),
                mapOf("name" to "With wrong interval", "url" to "http://www.seznam.cz/", "interval" to "wrong interval"),
                mapOf("name" to "Without interval", "url" to "tcp://www.seznam.cz/"),
                mapOf("name" to "Without url", "interval" to 5),
                mapOf("url" to "http://www.seznam.cz/", "interval" to 5)
        )

        for (requestData in requestDataList) {
            val request = MockMvcRequestBuilders
                    .put("/monitor/endpoint/1")
                    .content(jacksonObjectMapper().writeValueAsString(requestData))
                    .contentType(APPLICATION_JSON_UTF8)

            mockMvc!!.perform(request)
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
                    .andReturn()
        }
    }

    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `update monitored endpoint with not existing id`() {
        val requestData = MonitoredEndpointRequest(name = "Seznam", url = "http://www.seznam.cz/", interval = 5)

        val request = MockMvcRequestBuilders
                .put("/monitor/endpoint/9")
                .content(jacksonObjectMapper().writeValueAsString(requestData))
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @WithUserDetails("michal@example.com", userDetailsServiceBeanName = "authService")
    fun `update monitored endpoint with different owner`() {
        val requestData = MonitoredEndpointRequest(name = "Seznam", url = "http://www.seznam.cz/", interval = 5)

        val request = MockMvcRequestBuilders
                .put("/monitor/endpoint/1")
                .content(jacksonObjectMapper().writeValueAsString(requestData))
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun `update monitored endpoint with wrong token`() {
        val requestData = MonitoredEndpointRequest(name = "Seznam", url = "http://www.seznam.cz/", interval = 5)
        val request = MockMvcRequestBuilders
                .put("/monitor/endpoint/1")
                .header("Authorization", "Bearer: wrongToken")
                .content(jacksonObjectMapper().writeValueAsString(requestData))
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andReturn()
    }

    @Test
    fun `update monitored endpoint without token`() {
        val requestData = MonitoredEndpointRequest(name = "Seznam", url = "http://www.seznam.cz/", interval = 5)
        val request = MockMvcRequestBuilders
                .put("/monitor/endpoint/1")
                .content(jacksonObjectMapper().writeValueAsString(requestData))
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andReturn()
    }
}
