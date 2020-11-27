package com.example.restservice.monitor

import com.example.restservice.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

data class MonitoredEndpointRequest(val id: Long?, val name: String?, val url: String?, val interval: Int?)


@Controller
@RequestMapping(path = ["/MonitoredEndpoints"])
class MonitoredEndpointController {

    @Autowired
    private val monitoredEndpointRepository: MonitoredEndpointRepository? = null

    @Autowired
    private val userRepository: UserRepository? = null


    @ResponseBody
    @PostMapping(path = ["/"])
    fun addNewMonitoredEndpoint(authentication: Authentication, @RequestBody monitoredEndpointRequest: MonitoredEndpointRequest): ResponseEntity<String> {
        monitoredEndpointRepository!!.save(
                MonitoredEndpoint(
                        name = monitoredEndpointRequest.name ?: throw IllegalArgumentException("Name is required"),
                        url = monitoredEndpointRequest.url ?: throw IllegalArgumentException("Url is required"),
                        monitoredInterval = monitoredEndpointRequest.interval ?: throw IllegalArgumentException("Interval is required"),
                        owner = userRepository!!.findByEmail(authentication.name)
                )
        )
        return ResponseEntity.ok("Saved.")
    }


    @ResponseBody
    @PutMapping(path = ["/"])
    fun updateMonitoredEndpoint(authentication: Authentication, @RequestBody monitoredEndpointRequest: MonitoredEndpointRequest): ResponseEntity<String> {
        val monitoredEndpoint = monitoredEndpointRepository!!.findById(monitoredEndpointRequest.id ?: throw IllegalArgumentException("Id is required")).get()
        if (!monitoredEndpointRequest.name.isNullOrBlank()) { monitoredEndpoint.name = monitoredEndpointRequest.name }
        if (!monitoredEndpointRequest.url.isNullOrBlank()) { monitoredEndpoint.url = monitoredEndpointRequest.url }
        if (monitoredEndpointRequest.interval != null) { monitoredEndpoint.monitoredInterval = monitoredEndpointRequest.interval }

        if (authentication.name != monitoredEndpoint.owner!!.email) {
            return ResponseEntity.status(403).body("You are not owner.")
        }

        monitoredEndpointRepository.save(monitoredEndpoint)
        return ResponseEntity.ok("Updated.")
    }

    @ResponseBody
    @GetMapping(path = ["/"])
    fun allMonitoredEndpoints(authentication: Authentication, @RequestParam(defaultValue = "0") page: Int?, @RequestParam(defaultValue = "10") size: Int?) : ResponseEntity<List<MonitoredEndpoint>?> {
        val user = userRepository!!.findByEmail(authentication.name)

        return ResponseEntity.ok(monitoredEndpointRepository!!.findByOwner(user.id!!, PageRequest.of(page!!, size!!)))
    }

    @ResponseBody
    @DeleteMapping(path = ["/"])
    fun deleteMonitoredEndpoint(authentication: Authentication, id: Long) : ResponseEntity<String> {
        val monitoredEndpoint = monitoredEndpointRepository!!.findById(id).get()

        if (authentication.name != monitoredEndpoint.owner!!.email) {
            return ResponseEntity.status(403).body("You are not owner.")
        }
        monitoredEndpointRepository.deleteById(id)
        return ResponseEntity.ok("Deleted.")
    }
}



