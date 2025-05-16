package com.cota.mapping.service

import org.springframework.beans.factory.annotation.Value

object Endpoints {
    object Mapping {
        const val GetAll = "/getMappings"
        const val GetByVersion = "/byVersion"
        const val Add = "/add"
        const val Delete = "/delete"
        const val Update = "/replace"
    }

}