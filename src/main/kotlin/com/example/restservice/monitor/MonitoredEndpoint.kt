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

    @Column(nullable = false)
    var name: String? = null,

    @Column(nullable = false)
    var url: String? = null,

    @Column(updatable = false)
    var created: LocalDateTime = LocalDateTime.now(),

    var lastCheck: LocalDateTime? = null,

    @Column(nullable = false)
    var monitoredInterval: Int? = null,

    @JsonIgnore
    @ManyToOne
    var owner: User? = null
)

