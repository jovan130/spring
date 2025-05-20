package com.cota.mapping.service

import com.cota.mapping.entity.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class MappingService(
        private val restTemplate: RestTemplate
) {
    @Value("\${mapping.resource.base_url}")
    val mapping_resource_base_url = "https://storage.cloud.google.com/prodtest-us-5g-udp2-4-dxh-test-multiregion"

    @Value("\${mapping.api.base_url}")
    val mapping_base_url = "https://external-api.com/mapping"

    fun fetchMappings(): ApiResult<List<MappingEntry>> {
        return try {
            val response = restTemplate.exchange(
                    "${mapping_base_url}${Constants.Mapping.GetAll}",
                    HttpMethod.GET,
                    HttpEntity(null, getHttpHeaders()),
                    object : ParameterizedTypeReference<List<MappingEntry>>() {}
            )
            val list: List<MappingEntry> = response.body ?: emptyList()
            ApiResult(data = list.map { item -> item.copy(filename = item.filename) })
        } catch (e: Exception) {
            ApiResult(null, error = e.message)
        }
    }

    fun getPaginatedMappings(request: DataTableRequest): DataTableResponse<MappingEntry> {
        val allPosts = fetchMappings().data

        if (allPosts == null) {
            return DataTableResponse(
                    draw = request.draw,
                    recordsTotal = 0,
                    recordsFiltered = 0,
                    data = null
            )
        } else {
            val filtered = request.search?.value?.let { term ->
                allPosts.filter {
                    it.appVersion.contains(term, ignoreCase = true) || it.comments.contains(term, ignoreCase = true) || it.filename.contains(term, ignoreCase = true)
                }
            } ?: allPosts

            val sorted = request.order?.firstOrNull()?.let { order ->
                val columnName = request.columns?.get(order.column)?.data ?: "appVersion"
                when (columnName) {
                    "appVersion" -> if (order.dir == "asc") filtered.sortedBy { it.appVersion } else filtered.sortedByDescending { it.appVersion }
                    "filename" -> if (order.dir == "asc") filtered.sortedBy { it.filename } else filtered.sortedByDescending { it.filename }
                    "comments" -> if (order.dir == "asc") filtered.sortedBy { it.comments } else filtered.sortedByDescending { it.comments }
                    "dateUploaded" -> if (order.dir == "asc") filtered.sortedBy { it.dateUploaded } else filtered.sortedByDescending { it.dateUploaded }
                    else -> filtered
                }
            } ?: filtered

            val paginated = sorted.drop(request.start).take(request.length)

            return DataTableResponse(
                    draw = request.draw,
                    recordsTotal = allPosts.size.toLong(),
                    recordsFiltered = filtered.size.toLong(),
                    data = paginated
            )
        }
    }

    fun getByVersion(version: String): ApiResult<MappingEntry> {
        return try {
            val response = restTemplate.exchange(
                    "${mapping_base_url}${Constants.Mapping.GetByVersion}",
                    HttpMethod.GET,
                    HttpEntity(AppVersionRequest(version), getHttpHeaders()),
                    object : ParameterizedTypeReference<MappingEntry>() {}
            )
            ApiResult(data = response.body)
        } catch (e: Exception) {
            ApiResult(error = e.message)
        }
    }

    fun addMapping(info: MappingEntry, file: MultipartFile?): ApiResult<String> {
        return try {
            val uploadResult = uploadFirmware(file, info.filename)
            if (uploadResult.error == null) {
                info.filename = uploadResult.data.toString()
                val response = restTemplate.exchange(
                        "${mapping_base_url}${Constants.Mapping.Add}",
                        HttpMethod.POST,
                        HttpEntity(info, getHttpHeaders()),
                        object : ParameterizedTypeReference<String>() {}
                )
                ApiResult(data = response.body)
            } else {
                ApiResult(error = uploadResult.error)
            }
        } catch (e: Exception) {
            ApiResult(error = e.message)
        }
    }

    fun uploadFirmware(firmwareBinary: MultipartFile?, filePath: String?): ApiResult<String> {
        val info = FirmwareEntry(
                firmwareBinary = firmwareBinary?.bytes,
                filePath = filePath
        )
        return try {
            if (firmwareBinary != null) {
                val response = restTemplate.exchange(
                        "${mapping_base_url}${Constants.Mapping.Upload}",
                        HttpMethod.POST,
                        HttpEntity(info, getHttpHeaders()),
                        object : ParameterizedTypeReference<String>() {}
                )
                ApiResult(data = response.body)
            } else {
                ApiResult(data = filePath)
            }

        } catch (e: Exception) {
            ApiResult(error = e.message)
        }
    }

    fun deleteMapping(version: String): ApiResult<String> {
        return try {
            val response = restTemplate.exchange(
                    "${mapping_base_url}${Constants.Mapping.Delete}",
                    HttpMethod.POST,
                    HttpEntity(AppVersionRequest(version), getHttpHeaders()),
                    object : ParameterizedTypeReference<String>() {}
            )
            ApiResult(data = response.body)
        } catch (e: Exception) {
            ApiResult(error = e.message)
        }
    }

    fun getHttpHeaders(): HttpHeaders {
        val traceId = UUID.randomUUID().toString()
        val accountId = UUID.randomUUID().toString()
        val softwareId = UUID.randomUUID().toString()

        val headers = HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json")
        headers.add(Constants.HeaderKeys.TRACE_ID, traceId)
        headers.add(Constants.HeaderKeys.ACCOUNT_ID, accountId)
        headers.add(Constants.HeaderKeys.SOFTWARE_ID, softwareId)

        return headers
    }

    fun fetchDummyMappings(): List<MappingEntry> {
        val list = listOf(
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = "sefsefse"),
                MappingEntry(appVersion = "1.3.0", filename = "sfsef.bin", dateUploaded = "2025.05.06", comments = "sefsefsef"),
        );
        return list.map { item -> item.copy(filename = item.filename) }
    }
}