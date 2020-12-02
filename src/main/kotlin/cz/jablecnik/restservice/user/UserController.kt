package cz.jablecnik.restservice.user

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
class UserController {


    @Autowired
    private val userRepository: UserRepository? = null

    @ResponseBody
    @PostMapping(path = ["/user"])
    fun addNewUser(@RequestBody @Valid userRequest: UserRequest): ResponseEntity<String> {
        val u = User(null, userRequest.name, userRequest.email, passwordHash = BCryptPasswordEncoder().encode(userRequest.password))
        userRepository!!.save(u)
        return ResponseEntity.status(HttpStatus.CREATED).body("Created")
    }

    @get:ResponseBody
    @get:GetMapping(path = ["/user"])
    val allUsers: ResponseEntity<Map<String, Any>>
        get() = ResponseEntity.ok(mapOf("users" to userRepository!!.findAll().toList()))


    @ResponseBody
    @DeleteMapping(path = ["/user/{id}"])
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Any> {
        userRepository!!.deleteById(id)
        return ResponseEntity.ok("Deleted")
    }
}



