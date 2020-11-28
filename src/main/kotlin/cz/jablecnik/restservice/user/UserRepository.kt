package cz.jablecnik.restservice.user

import org.springframework.data.repository.CrudRepository


interface UserRepository : CrudRepository<User?, Long?> {


    fun findByName(name: String): User

    fun findByEmail(email: String): User
}
