package com.example.restservice

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User?, Long?>
