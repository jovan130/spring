package com.cota.mapping.service

object Constants {
    object Mapping {
        const val GetAll = "mapping/getMappings"
        const val GetByVersion = "mapping/byVersion"
        const val Add = "mapping/add"
        const val Delete = "mapping/delete"
        const val Update = "mapping/replace"
        const val Upload = "firmware/uploadFirmware"
    }

    object HeaderKeys {
        const val TRACE_ID = "X-Trace-Id"
        const val ACCOUNT_ID = "X-Account-Id"
        const val SOFTWARE_ID = "X-Software-Id"
    }

}