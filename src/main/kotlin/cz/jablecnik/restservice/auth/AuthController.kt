package cz.jablecnik.restservice.auth

import cz.jablecnik.restservice.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.lang.Exception


data class JwtRequest (val username: String?, val password: String? )

data class JwtResponse(val name: String, val email: String, val token: String)


@RestController
@CrossOrigin
class AuthController {


    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val jwtTokenUtil: JwtTokenUtil? = null

    @Autowired
    private val authService: AuthService? = null

    @Autowired
    private val userRepository: UserRepository? = null


    @RequestMapping(value = ["/authenticate"], method = [RequestMethod.POST])
    @Throws(Exception::class)
    fun createAuthenticationToken(@RequestBody authenticationRequest: JwtRequest): ResponseEntity<*> {
        authenticate(authenticationRequest.username!!, authenticationRequest.password!!)
        val userDetails = authService!!.loadUserByUsername(authenticationRequest.username)
        val user = userRepository!!.findByEmail(authenticationRequest.username)

        val token: String = jwtTokenUtil!!.generateToken(userDetails)
        return ResponseEntity.ok<Any>(JwtResponse(user.name!!, user.email!!, token))
    }

    @Throws(Exception::class)
    private fun authenticate(username: String, password: String): Unit {
        try {
            authenticationManager!!.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (e: DisabledException) {
            throw Exception("USER_DISABLED", e)
        } catch (e: BadCredentialsException) {
            throw Exception("INVALID_CREDENTIALS", e)
        }
    }
}

