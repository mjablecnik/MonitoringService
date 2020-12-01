package cz.jablecnik.restservice

import cz.jablecnik.restservice.auth.JwtTokenUtil
import cz.jablecnik.restservice.monitor.MonitorController
import cz.jablecnik.restservice.monitor.MonitorScheduler
import cz.jablecnik.restservice.monitor.MonitoredEndpointRepository
import cz.jablecnik.restservice.monitor.MonitoringResultRepository
import cz.jablecnik.restservice.user.UserRepository
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc


val APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8"


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = ["cz.jablecnik.restservice.monitor", "cz.jablecnik.restservice.user", "cz.jablecnik.restservice.auth"])
open class TestConfig {


    @Bean
    open fun jwtTokenUtil(): JwtTokenUtil {
        return Mockito.mock(JwtTokenUtil::class.java)
    }

    @Bean
    open fun userRepository(): UserRepository {
        return Mockito.mock(UserRepository::class.java)
    }

    @Bean
    open fun monitorController(): MonitorController {
        return MonitorController()
    }

    @Bean
    open fun monitoredEndpointRepository(): MonitoredEndpointRepository {
        return Mockito.mock(MonitoredEndpointRepository::class.java)
    }

    @Bean
    open fun monitoringResultRepository(): MonitoringResultRepository {
        return Mockito.mock(MonitoringResultRepository::class.java)
    }

    @Bean
    open fun monitorScheduler(): MonitorScheduler {
        return MonitorScheduler()
    }
}
