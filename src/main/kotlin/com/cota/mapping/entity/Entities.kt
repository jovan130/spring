package com.cota.mapping.entity

data class MappingEntry(
        var appVersion: String,
        var filename: String,
        var dateUploaded: String,
        var comments: String,
        var full_path: String? = null
)

data class AppVersionRequest(
        val appVersion: String? = null
)

data class FirmwareEntry(
        val firmwareBinary: ByteArray? = null,
        val filePath: String? = null
)

data class ApiResult<T>(
        val data: T? = null,
        val error: String? = null
)

