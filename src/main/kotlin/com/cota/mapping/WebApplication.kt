package com.cota.mapping

import com.cota.mapping.config.WebProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(WebProperties::class)
class WebApplication

fun main(args: Array<String>) {
	runApplication<WebApplication>(*args)
}
