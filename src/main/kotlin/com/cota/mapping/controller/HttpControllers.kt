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
            @RequestParam comment: String,
            @RequestParam filename: String,
            @RequestParam dateUploaded: String,
            @RequestParam(required = false) file: MultipartFile?
    ): ResponseEntity<Map<String, String>> {
        return try {
            val currentDate = LocalDate.now().toString().replace("-", ".")

            // Todo save the mapping using external api.

            ResponseEntity.ok(mapOf("message" to "Post created successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(mapOf("error" to "Failed to create mapping entry"))
        }
    }

    @PostMapping("/mapping_table", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getPosts(@RequestBody request: DataTableRequest): DataTableResponse<MappingEntry> {
        return mappingService.getPaginatedMappings(request)
    }
}