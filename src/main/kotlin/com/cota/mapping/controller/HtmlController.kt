package com.cota.mapping.controller

import com.cota.mapping.config.WebProperties
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HtmlController(private val properties: WebProperties) {

    @GetMapping("/")
    fun home(model: Model): String {
        model["title"] = properties.title
        return "home"
    }
}
