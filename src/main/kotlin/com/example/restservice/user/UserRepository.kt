package com.example.restservice.user

import org.springframework.data.repository.CrudRepository


interface UserRepository : CrudRepository<User?, Long?> {

    //@Query(value = "SELECT user from User user where user.name = :name", nativeQuery = false)
    //fun findByName(@Param("name") name: String): User

    fun findByName(name: String): User

    fun findByEmail(email: String): User
}
