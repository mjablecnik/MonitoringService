package cz.jablecnik.restservice.monitor

import kong.unirest.Unirest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.transaction.Transactional


@EnableAsync
@Component
open class MonitorScheduler {

    @Autowired
    private val monitoredEndpointRepository: MonitoredEndpointRepository? = null

    @Autowired
    private val monitoringResultRepository: MonitoringResultRepository? = null


    @Async
    @Transactional
    @Scheduled(fixedRate = 1000)
    @Throws(InterruptedException::class)
    open fun monitorEndpoints() {
        val monitoredEndpoints = monitoredEndpointRepository!!.findAll().toList()
        val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        for (me in monitoredEndpoints) {
            if (me == null) continue

            if (me.lastCheck == null || me.lastCheck!!.toEpochSecond(ZoneOffset.UTC) <= currentTime - me.monitoredInterval!!.toLong()) {
                Unirest.get(me.url).asStringAsync {
                    me.lastCheck = LocalDateTime.now()
                    monitoredEndpointRepository.save(me)

                    monitoringResultRepository!!.save(
                            MonitoringResult(
                                    returnedHttpStatusCode = it.status,
                                    returnedPayload = it.body,
                                    monitoredEndpoint = me
                            )
                    )
                }
            }
        }
    }
}