/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.jablecnik.restservice

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import cz.jablecnik.restservice.auth.JwtRequest
import cz.jablecnik.restservice.auth.JwtResponse
import cz.jablecnik.restservice.user.User
import cz.jablecnik.restservice.user.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
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
open class UserAuthenticationTests {


    @Autowired
    private var mockMvc: MockMvc? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @PostConstruct
    open fun setup() {
        val userList = listOf(
                User(id = 1, name = "Martin", email = "martin@example.com", passwordHash = "testHash1"),
                User(id = 2, name = "Michal", email = "michal@example.com", passwordHash = "testHash2"),
                User(id = 3, name = "Aneta", email = "aneta@example.com", passwordHash = "testHash3")
        )
        Mockito.`when`(userRepository!!.findAll()).thenReturn(userList)
        Mockito.`when`(userRepository.findByEmail("martin@example.com"))
                .thenReturn(User(id = 1, name = "Martin", email = "martin@example.com", passwordHash = "\$2a\$10\$OYYkXEap4nbGclMTVwdyqeCX5czN83Gcil4SXDwgIEoIsLdk.aZSW"))
    }

    @Test
    fun getToken(): String {
        val requestData = JwtRequest(username = "martin@example.com", password = "vK83ffVh4e")

        val request = MockMvcRequestBuilders
                .post("/authenticate")
                .content(jacksonObjectMapper().writeValueAsString(requestData))
                .contentType(MediaType.APPLICATION_JSON)

        val response = mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        val responseData = jacksonObjectMapper().readValue(response.response.contentAsString, JwtResponse::class.java)

        return responseData.token
    }

    @Test
    fun `test authenticate endpoint with right password`() {
        val requestData = JwtRequest(username = "martin@example.com", password = "vK83ffVh4e")

        val request = MockMvcRequestBuilders
                .post("/authenticate")
                .content(jacksonObjectMapper().writeValueAsString(requestData))
                .contentType(MediaType.APPLICATION_JSON)

        val response = mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Martin"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("martin@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists())
                .andReturn()

        val responseData = jacksonObjectMapper().readValue(response.response.contentAsString, JwtResponse::class.java)

        Assertions.assertEquals(191, responseData.token.length)
    }

    @Test
    fun `test authenticate endpoint with wrong password`() {
        val requestData = JwtRequest(username = "martin@example.com", password = "test123")

        val request = MockMvcRequestBuilders
                .post("/authenticate")
                .content(jacksonObjectMapper().writeValueAsString(requestData))
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `test authenticate endpoint without content data`() {
        val request = MockMvcRequestBuilders
                .post("/authenticate")

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    @WithUserDetails("martin@example.com", userDetailsServiceBeanName = "authService")
    fun `test user list endpoint`() {
        val request = MockMvcRequestBuilders
                .get("/user")
                .header("Authorization", "Bearer: ${getToken()}")
                .contentType(MediaType.APPLICATION_JSON)

        val response = mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.users").exists())
                .andReturn()

        val expectedResponseData = Gson().toJsonTree(
                mapOf("users" to listOf(
                        User(id = 1, name = "Martin", email = "martin@example.com"),
                        User(id = 2, name = "Michal", email = "michal@example.com"),
                        User(id = 3, name = "Aneta", email = "aneta@example.com")
                ))
        ).toString()
        Assertions.assertEquals(expectedResponseData, response.response.contentAsString)
    }

    @Test
    fun `test user list endpoint with wrong token`() {
        val request = MockMvcRequestBuilders
                .get("/user")
                .header("Authorization", "Bearer: wrongToken")
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andReturn()
    }

    @Test
    fun `test user list endpoint without token`() {
        val request = MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc!!.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andReturn()
    }
}