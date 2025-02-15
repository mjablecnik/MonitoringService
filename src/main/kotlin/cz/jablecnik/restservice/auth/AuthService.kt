package cz.jablecnik.restservice.auth

import cz.jablecnik.restservice.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*



@Service
open class AuthService : UserDetailsService {

    @Autowired
    private val userRepository: UserRepository? = null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository!!.findByEmail(username)
        return if (user.email == username) {
            User(user.email, user.passwordHash, ArrayList())
        } else {
            throw UsernameNotFoundException("User not found with username: $username")
        }
    }
}

