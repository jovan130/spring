package com.cota.mapping.controller

import com.cota.mapping.config.WebProperties
import com.cota.mapping.entity.MappingEntry
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HtmlController(private val properties: WebProperties) {

    @GetMapping("/")
    fun blog(model: Model): String {
        val posts = listOf(
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),

                // Add more posts or fetch from repository
        )
        model["title"] = properties.title
        model["posts"] = posts
        return "home"
    }
}
