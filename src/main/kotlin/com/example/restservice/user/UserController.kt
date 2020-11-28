package com.example.restservice.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.*


data class UserRequest(
        val id: Long?,
        @field:Size(min = 3)
        val name: String? = null,
        @field:Email
        val email: String,
        @field:Size(min = 10)
        val password: String
)

@Controller
@RequestMapping(path = ["/user"])
class UserController {


    @Autowired
    private val userRepository: UserRepository? = null

    @ResponseBody
    @PostMapping(path = ["/"])
    fun addNewUser(@RequestBody @Valid userRequest: UserRequest): ResponseEntity<String> {
        val u = User(null, userRequest.name, userRequest.email, passwordHash = BCryptPasswordEncoder().encode(userRequest.password))
        userRepository!!.save(u)
        return ResponseEntity.status(HttpStatus.CREATED).body("Saved.")
    }

    @get:ResponseBody
    @get:GetMapping(path = ["/"])
    val allUsers: ResponseEntity<MutableIterable<User?>>
        get() = ResponseEntity.ok(userRepository!!.findAll())


    @ResponseBody
    @GetMapping(path = ["/{id}/"])
    fun getUser(@PathVariable id: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(userRepository!!.findById(id))
    }
}



