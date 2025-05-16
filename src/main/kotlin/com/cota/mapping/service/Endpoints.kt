package com.cota.mapping.service

import org.springframework.beans.factory.annotation.Value

object Endpoints {

    @Value("\${mapping.api.base_url}")
    const val mapping_base_url = "https://external-api.com/mapping"

    object Mapping {
        const val GetAll = "${mapping_base_url}/getMappings"
        const val GetByVersion = "${mapping_base_url}/byVersion"
        const val Add = "${mapping_base_url}/add"
        const val Delete = "${mapping_base_url}/delete"
        const val Relace = "${mapping_base_url}/replace"
    }

}