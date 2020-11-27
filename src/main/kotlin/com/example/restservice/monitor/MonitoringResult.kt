package com.example.restservice.monitor

import java.time.LocalDateTime
import javax.persistence.*



@Entity
data class MonitoringResult (


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var dateOfCheck: LocalDateTime = LocalDateTime.now(),
    var returnedHttpStatusCode: Int? = null,

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false, updatable = false)
    var returnedPayload: String? = null,

    @OneToOne
    var monitoredEndpoint: MonitoredEndpoint? = null
)

