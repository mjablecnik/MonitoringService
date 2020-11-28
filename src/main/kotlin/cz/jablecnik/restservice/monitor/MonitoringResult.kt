package cz.jablecnik.restservice.monitor

import com.fasterxml.jackson.annotation.JsonIgnore
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
    @JsonIgnore
    @OneToOne
    var monitoredEndpoint: MonitoredEndpoint? = null
)

