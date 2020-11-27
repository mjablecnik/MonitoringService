package com.example.restservice.monitor

import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull



@Entity
data class MonitoringResult (


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(updatable = false)
    var dateOfCheck: LocalDateTime = LocalDateTime.now(),

    @NotNull
    @Column(nullable = false, updatable = false)
    var returnedHttpStatusCode: Int? = null,

    @NotNull
    @Column(columnDefinition = "MEDIUMTEXT", nullable = false, updatable = false)
    var returnedPayload: String? = null,

    @NotNull
    @OneToOne
    var monitoredEndpoint: MonitoredEndpoint? = null
)

