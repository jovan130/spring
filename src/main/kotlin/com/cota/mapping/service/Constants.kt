package com.cota.mapping.service

object Constants {
    object Mapping {
        const val GetAll = "/getMappings"
        const val GetByVersion = "/byVersion"
        const val Add = "/add"
        const val Delete = "/delete"
        const val Update = "/replace"
    }

    object HeaderKeys {
        const val TRACE_ID = "X-Trace-Id"
        const val ACCOUNT_ID = "X-Account-Id"
        const val SOFTWARE_ID = "X-Software-Id"
    }

}