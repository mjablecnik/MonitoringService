package com.example.restservice.monitor

import com.example.restservice.user.User
import com.fasterxml.jackson.annotation.*
import java.time.LocalDateTime
import javax.persistence.*



@Entity
data class MonitoredEndpoint constructor(


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var name: String? = null,
    var url: String? = null,

    var created: LocalDateTime = LocalDateTime.now(),
    var lastCheck: LocalDateTime? = null,
    var monitoredInterval: Int? = null,

    @JsonIgnore
    @ManyToOne
    var owner: User? = null
)

