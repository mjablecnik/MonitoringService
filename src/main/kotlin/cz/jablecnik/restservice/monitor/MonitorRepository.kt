package cz.jablecnik.restservice.monitor

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface MonitoringResultRepository : CrudRepository<MonitoringResult?, Long?> {

    @Query(value = "SELECT * from monitoring_result mr where mr.monitored_endpoint_id = :monitoredEndpointId", nativeQuery = true)
    fun findResults(@Param("monitoredEndpointId") monitoredEndpointId: Long, pageable: Pageable): List<MonitoringResult>?
}

interface MonitoredEndpointRepository : CrudRepository<MonitoredEndpoint?, Long?> {

    @Query(value = "SELECT * from monitored_endpoint me where me.owner_id = :userId", nativeQuery = true)
    fun findByOwner(@Param("userId") userId: Long, pageable: Pageable): List<MonitoredEndpoint>?

    @Query(value = "SELECT me from MonitoredEndpoint me where me.id = :id", nativeQuery = false)
    fun findById(@Param("id") id: Long): MonitoredEndpoint?
}
