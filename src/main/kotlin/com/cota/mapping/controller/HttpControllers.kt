package com.cota.mapping.controller

import com.cota.mapping.entity.DataTableRequest
import com.cota.mapping.entity.DataTableResponse
import com.cota.mapping.entity.MappingEntry
import com.cota.mapping.service.MappingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
@RequestMapping("/api")
class ArticleController() {

    @Autowired
    private lateinit var mappingService: MappingService

    @PostMapping("/save")
    fun save(
            @RequestParam version: String,
            @RequestParam(required = false) comment: String,
            @RequestParam(required = false) filename: String,
            @RequestParam(required = false) dateUploaded: String,
            @RequestParam(required = false) file: MultipartFile?
    ): ResponseEntity<Map<String, String>> {
        return try {
            var currentDate = LocalDate.now().toString().replace("-", ".")
            if (dateUploaded.isNotBlank()) {
                currentDate = dateUploaded
            }

            val info = MappingEntry(
                    appVersion = version,
                    comments = comment,
                    dateUploaded = currentDate,
                    filename = filename
            )

            val result = mappingService.addMapping(info, file)
            if (result.error == null) {
                ResponseEntity.ok(mapOf("message" to "Post created successfully"))
            } else {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(mapOf("error" to "${result.error}"))
            }

        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(mapOf("error" to "Failed to create mapping entry"))
        }
    }

    @PostMapping("/delete")
    fun delete(
            @RequestParam version: String,
    ): ResponseEntity<Map<String, String>> {
        return try {
            val result = mappingService.deleteMapping(version)
            if (result.error == null) {
                ResponseEntity.ok(mapOf("message" to "Mapping entry has been removed successfully"))
            } else {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(mapOf("error" to "${result.error}"))
            }

        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(mapOf("error" to "Failed to remove mapping entry"))
        }
    }

    @PostMapping("/mapping_table", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getPosts(@RequestBody request: DataTableRequest): DataTableResponse<MappingEntry> {
        return mappingService.getPaginatedMappings(request)
    }


}