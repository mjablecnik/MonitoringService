package cz.jablecnik.restservice.monitor

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
open class DeleteMonitoredEndpointTests {

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

        Mockito.`when`(userRepository!!.findByEmail("martin@example.com"))
                .thenReturn(firstUser)

        Mockito.`when`(userRepository.findByEmail("michal@example.com"))
                .thenReturn(secondUser)
    }


    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `delete monitored endpoint`() {
        val request = MockMvcRequestBuilders
                .delete("/monitor/endpoint/1")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `delete monitored endpoint with not existing id`() {
        val request = MockMvcRequestBuilders
                .delete("/monitor/endpoint/9")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @WithUserDetails("michal@example.com", userDetailsServiceBeanName = "authService")
    fun `delete monitored endpoint with different owner`() {
        val request = MockMvcRequestBuilders
                .delete("/monitor/endpoint/1")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun `delete monitored endpoint with wrong token`() {
        val request = MockMvcRequestBuilders
                .delete("/monitor/endpoint/1")
                .header("Authorization", "Bearer: wrongToken")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andReturn()
    }

    @Test
    fun `delete monitored endpoint without token`() {
        val request = MockMvcRequestBuilders
                .delete("/monitor/endpoint/1")
                .contentType(APPLICATION_JSON_UTF8)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andReturn()
    }
}
