package com.cota.mapping.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("web")
data class WebProperties(var title: String, val banner: Banner) {
	data class Banner(val title: String? = null, val content: String)
}
