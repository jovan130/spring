package com.cota.mapping.service

import com.cota.mapping.entity.DataTableRequest
import com.cota.mapping.entity.DataTableResponse
import com.cota.mapping.entity.MappingEntry
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class MappingService(
        private val restTemplate: RestTemplate
) {
    fun fetchMappings(): List<MappingEntry> {
        val response = restTemplate.exchange(
                "https://external-api.com/posts", // replace with your actual URL
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<MappingEntry>>() {}
        )
        return response.body ?: emptyList()
    }

    fun getPaginatedMappings(request: DataTableRequest): DataTableResponse<MappingEntry> {
        val allPosts = fetchDummyMappings()

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


    fun fetchDummyMappings(): List<MappingEntry> {
        val list = listOf(
                MappingEntry(appVersion = "1.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.3.0", filename = "sfsef.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.4.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.5.0", filename = "drg.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.6.0", filename = "yjgyjy.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "2.3.0", filename = "rdg.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "4.1.0", filename = "binsfefsary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "2.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "3.8.0", filename = "eretrt.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.6.0", filename = "yjgyjy.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "2.3.0", filename = "rdg.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "4.1.0", filename = "binsfefsary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "2.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "3.8.0", filename = "eretrt.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.6.0", filename = "yjgyjy.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "2.3.0", filename = "rdg.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "4.1.0", filename = "binsfefsary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "2.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "3.8.0", filename = "eretrt.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.6.0", filename = "yjgyjy.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "2.3.0", filename = "rdg.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "4.1.0", filename = "binsfefsary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "2.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "3.8.0", filename = "eretrt.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "1.6.0", filename = "yjgyjy.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "2.3.0", filename = "rdg.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "4.1.0", filename = "binsfefsary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "2.1.0", filename = "binary.bin", dateUploaded = "2025.05.06", comments = ""),
                MappingEntry(appVersion = "3.8.0", filename = "eretrt.bin", dateUploaded = "2025.05.06", comments = ""),
        );
        return list
    }
}