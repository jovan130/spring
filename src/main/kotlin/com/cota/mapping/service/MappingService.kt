package com.cota.mapping.service

import com.cota.mapping.entity.AppVersionRequest
import com.cota.mapping.entity.DataTableRequest
import com.cota.mapping.entity.DataTableResponse
import com.cota.mapping.entity.MappingEntry
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.time.OffsetDateTime
import java.util.*

@Service
class MappingService(
        private val restTemplate: RestTemplate
) {
    @Value("\${mapping.resource.base_url}")
    val mapping_resource_base_url = "https://storage.cloud.google.com/prodtest-us-5g-udp2-4-dxh-test-multiregion"

    @Value("\${mapping.api.base_url}")
    val mapping_base_url = "https://external-api.com/mapping"

    fun fetchMappings(): List<MappingEntry> {
        val response = restTemplate.exchange(
                "${mapping_base_url}${Constants.Mapping.GetAll}",
                HttpMethod.GET,
                HttpEntity(null, getHttpHeaders()),
                object : ParameterizedTypeReference<List<MappingEntry>>() {}
        )
        val list: List<MappingEntry> = response.body ?: emptyList()
        return list.map { item -> item.copy(full_path = "$mapping_resource_base_url/${item.filename}") }
    }

    fun getPaginatedMappings(request: DataTableRequest): DataTableResponse<MappingEntry> {
        val allPosts = fetchMappings()

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

    fun getByVersion(version: String): MappingEntry? {
        val response = restTemplate.exchange(
                "${mapping_base_url}${Constants.Mapping.GetByVersion}",
                HttpMethod.GET,
                HttpEntity(AppVersionRequest(version), getHttpHeaders()),
                object : ParameterizedTypeReference<MappingEntry>() {}
        )
        return response.body
    }

    fun addMapping(info: MappingEntry, file: MultipartFile?): String? {
        //Todo upload file to GCS and save the path to info

        val response = restTemplate.exchange(
                "${mapping_base_url}${Constants.Mapping.Add}",
                HttpMethod.POST,
                HttpEntity(info, getHttpHeaders()),
                object : ParameterizedTypeReference<String>() {}
        )
        return response.body
    }

    fun deleteMapping(version: String): String? {
        val response = restTemplate.exchange(
                "${mapping_base_url}${Constants.Mapping.Delete}",
                HttpMethod.POST,
                HttpEntity(AppVersionRequest(version), getHttpHeaders()),
                object : ParameterizedTypeReference<String>() {}
        )
        return response.body
    }

    fun getHttpHeaders(): HttpHeaders {
        val traceId = UUID.randomUUID().toString()
        val accountId = UUID.randomUUID().toString()
        val softwareId = UUID.randomUUID().toString()
        val requestTimestamp = OffsetDateTime.now().toString()

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
        return list
    }
}