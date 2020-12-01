package cz.jablecnik.restservice

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cz.jablecnik.restservice.monitor.MonitoredEndpointRequest
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
import javax.annotation.PostConstruct



@ContextConfiguration(classes = [TestConfig::class])
@AutoConfigureMockMvc
@SpringBootTest
open class MonitoringTests {

    @Autowired
    private var mockMvc: MockMvc? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @PostConstruct
    open fun setup() {
        Mockito.`when`(userRepository!!.findByEmail("martin@example.com"))
                .thenReturn(User(id = 1, name = "Martin", email = "martin@example.com", passwordHash = "\$2a\$10\$OYYkXEap4nbGclMTVwdyqeCX5czN83Gcil4SXDwgIEoIsLdk.aZSW"))
    }


    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `add new monitored endpoint`() {
        val requestDataList = listOf(
                MonitoredEndpointRequest(name = "Only http address", url = "http://www.seznam.cz/", interval = 5),
                MonitoredEndpointRequest(name = "Https address", url = "http://www.centrum.cz/", interval = 50),
                MonitoredEndpointRequest(name = "My ip address", url = "http://77.75.75.176", interval = 890),
                MonitoredEndpointRequest(name = "My ip address", url = "http://77.75.75.176:8080", interval = 890)
        )

        for (requestData in requestDataList) {
            val request = MockMvcRequestBuilders
                    .post("/monitor/endpoint")
                    .content(jacksonObjectMapper().writeValueAsString(requestData))
                    .contentType(APPLICATION_JSON_UTF8)

            mockMvc!!.perform(request)
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isCreated)
        }
    }

    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `add new monitored endpoint with wrong params`() {
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
                    .post("/monitor/endpoint")
                    .content(jacksonObjectMapper().writeValueAsString(requestData))
                    .contentType(APPLICATION_JSON_UTF8)

            mockMvc!!.perform(request)
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
                    .andReturn()
        }
    }

    @Test
    fun `add new monitored endpoint with wrong token`() {
        val requestData = MonitoredEndpointRequest(name = "Seznam", url = "http://www.seznam.cz/", interval = 5)
        val request = MockMvcRequestBuilders
                .post("/monitor/endpoint")
                .header("Authorization", "Bearer: wrongToken")
                .content(jacksonObjectMapper().writeValueAsString(requestData))
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andReturn()
    }

    @Test
    fun `add new monitored endpoint without token`() {
        val requestData = MonitoredEndpointRequest(name = "Seznam", url = "http://www.seznam.cz/", interval = 5)
        val request = MockMvcRequestBuilders
                .post("/monitor/endpoint")
                .content(jacksonObjectMapper().writeValueAsString(requestData))
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andReturn()
    }
}