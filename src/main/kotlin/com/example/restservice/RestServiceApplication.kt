package com.example.restservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@EnableScheduling
@SpringBootApplication
open class RestServiceApplication

fun main(args: Array<String>) {
    runApplication<RestServiceApplication>(*args)
}


