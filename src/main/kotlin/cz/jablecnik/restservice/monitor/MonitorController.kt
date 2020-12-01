package cz.jablecnik.restservice.monitor

import cz.jablecnik.restservice.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size



data class MonitoredEndpointRequest(
        @field:Size(min = 5, message = "Minimal name length is 5.")
        @field:NotNull(message = "Name param is required")
        val name: String? = null,

        @field:Pattern(regexp = "http(s)?://[a-z0-9-.:/]+", message = "Url address is in wrong format.")
        @field:NotNull(message = "Url param is required")
        val url: String? = null,

        @field:Min(1, message = "Minimal interval value must be 1.")
        @field:NotNull(message = "Interval param is required")
        val interval: Int? = null
)


@Controller
@RequestMapping(path = ["/monitor"])
class MonitorController {

    @Autowired
    private val monitoredEndpointRepository: MonitoredEndpointRepository? = null

    @Autowired
    private val userRepository: UserRepository? = null

    private fun checkValidationErrors(errors: Errors) {
        if (errors.hasErrors()) {
            if (errors.allErrors.size == 1) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, errors.allErrors.first().defaultMessage)
            } else {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation errors are: " + errors.allErrors.map { it.defaultMessage }.toString())
            }
        }
    }

    @ResponseBody
    @PostMapping(path = ["/endpoint"])
    fun addNewMonitoredEndpoint(authentication: Authentication, @Valid @RequestBody monitoredEndpointRequest: MonitoredEndpointRequest, errors: Errors): ResponseEntity<*> {
        checkValidationErrors(errors)

        monitoredEndpointRepository!!.save(
                MonitoredEndpoint(
                        name = monitoredEndpointRequest.name,
                        url = monitoredEndpointRequest.url,
                        monitoredInterval = monitoredEndpointRequest.interval,
                        owner = userRepository!!.findByEmail(authentication.name)
                )
        )
        return ResponseEntity.status(HttpStatus.CREATED).body("Saved.")
    }


    @ResponseBody
    @PutMapping(path = ["/endpoint/{id}"])
    fun updateMonitoredEndpoint(authentication: Authentication, @PathVariable id: Long, @Valid @RequestBody monitoredEndpointRequest: MonitoredEndpointRequest, errors: Errors): ResponseEntity<*> {
        checkValidationErrors(errors)

        val monitoredEndpoint = monitoredEndpointRepository!!.findById(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "MonitoredEndpoint with id $id doesn't exists.")
        monitoredEndpoint.name = monitoredEndpointRequest.name
        monitoredEndpoint.url = monitoredEndpointRequest.url
        monitoredEndpoint.monitoredInterval = monitoredEndpointRequest.interval

        if (authentication.name != monitoredEndpoint.owner!!.email) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not owner.")
        }

        monitoredEndpointRepository.save(monitoredEndpoint)
        return ResponseEntity.ok("Updated.")
    }

    @ResponseBody
    @DeleteMapping(path = ["/endpoint/{id}"])
    fun deleteMonitoredEndpoint(authentication: Authentication, @PathVariable id: Long) : ResponseEntity<*> {
        val monitoredEndpoint = monitoredEndpointRepository!!.findById(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "MonitoredEndpoint with id $id doesn't exists.")

        if (authentication.name != monitoredEndpoint.owner!!.email) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not owner.")
        }
        monitoredEndpointRepository.deleteById(id)
        return ResponseEntity.ok("Deleted")
    }

    @ResponseBody
    @GetMapping(path = ["/endpoint"])
    fun allMonitoredEndpoints(
            authentication: Authentication,
            @RequestParam(defaultValue = "1") page: Int?,
            @RequestParam(defaultValue = "10") size: Int?
    ) : ResponseEntity<*> {
        if (page!! < 1) { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Param page must be positive number.") }
        if (size!! < 1) { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Param size must be positive number.") }

        val user = userRepository!!.findByEmail(authentication.name)

        return ResponseEntity.ok(mapOf("endpoints" to monitoredEndpointRepository!!.findByOwner(user.id!!, PageRequest.of(page-1, size))))
    }

    @ResponseBody
    @GetMapping(path = ["/endpoint/{id}/result"])
    fun allMonitoringResults(
            authentication: Authentication,
            @PathVariable id: Long,
            @RequestParam(defaultValue = "1") page: Int?,
            @RequestParam(defaultValue = "10") size: Int?,
            @RequestParam(defaultValue = "desc") sort: String?
    ) : ResponseEntity<*> {
        if (page!! < 1) { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Param page must be positive number.") }
        if (size!! < 1) { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Param size must be positive number.") }
        if (!listOf("desc", "asc").contains(sort!!.toLowerCase())) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Param sort can contain only: 'DESC' or 'ASC' values.")
        }

        val monitoredEndpoint = monitoredEndpointRepository!!.findById(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "MonitoredEndpoint with id $id doesn't exists.")

        if (authentication.name != monitoredEndpoint.owner!!.email) {
            return ResponseEntity.status(403).body("You are not owner.")
        }

        val sortById = Sort.by(Sort.Direction.fromString(sort), "id")

        val results = monitoredEndpointRepository.findResults(
                monitoredEndpoint,
                PageRequest.of(page-1, size, sortById)
        )

        return ResponseEntity.ok(mapOf("results" to results))
    }
}
