package com.example.restservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*


@Controller
@RequestMapping(path = ["/users"])
class UserController {


    @Autowired
    private val userRepository: UserRepository? = null

    @ResponseBody
    @PostMapping(path = ["/"])
    fun addNewUser(@RequestParam name: String?, @RequestParam email: String?): String {
        val n = User()
        n.name = name
        n.email = email
        userRepository!!.save(n)
        return "Saved"
    }

    @get:ResponseBody
    @get:GetMapping(path = ["/"])
    val allUsers: Iterable<User?>
        get() = userRepository!!.findAll()


    @ResponseBody
    @GetMapping(path = ["/{id}/"])
    fun getUser(@PathVariable id: Long): Optional<User?> {
        return userRepository!!.findById(id)
    }
}



