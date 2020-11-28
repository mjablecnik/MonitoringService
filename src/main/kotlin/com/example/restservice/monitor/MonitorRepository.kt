package com.example.restservice.monitor

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface MonitoringResultRepository : CrudRepository<MonitoringResult?, Long?>

interface MonitoredEndpointRepository : CrudRepository<MonitoredEndpoint?, Long?> {

    @Query(value = "SELECT * from monitored_endpoint me where me.owner_id = :userId", nativeQuery = true)
    fun findByOwner(@Param("userId") userId: Long, pageable: Pageable): List<MonitoredEndpoint>?

    @Query(value = "SELECT mr from MonitoringResult mr where mr.monitoredEndpoint = :endpoint", nativeQuery = false)
    fun findResults(@Param("endpoint") monitoredEndpoint: MonitoredEndpoint, pageable: Pageable): List<MonitoringResult>?
}
