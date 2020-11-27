package com.example.restservice.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


@Controller
@RequestMapping(path = ["/users"])
class UserController {


    @Autowired
    private val userRepository: UserRepository? = null

    @ResponseBody
    @PostMapping(path = ["/"])
    fun addNewUser(@RequestParam name: String?, @RequestParam email: String?, @RequestParam password: String?): ResponseEntity<String> {
        val u = User(null, name, email, passwordHash = BCryptPasswordEncoder().encode(password))
        userRepository!!.save(u)
        return ResponseEntity.ok("Saved.")
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



