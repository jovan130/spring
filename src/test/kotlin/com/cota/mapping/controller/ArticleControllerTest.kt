package com.cota.mapping.controller

import com.cota.mapping.entity.DataTableRequest
import com.cota.mapping.service.MappingService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import java.time.LocalDate


@ExtendWith(MockitoExtension::class)
class ArticleControllerTest {

    @Mock
    private lateinit var mappingService: MappingService

    @InjectMocks
    private lateinit var articleController: ArticleController

    @Test
    fun `save should return success response when service succeeds`() {
        // Given
        val version = "1.0.0"
        val comment = "Test comment"
        val filename = "test.bin"
        val file = MockMultipartFile("file", "test.bin", "application/octet-stream", "test data".toByteArray())
        val expectedDate = LocalDate.now().toString().replace("-", ".")

        // When
        val response = articleController.save(version, comment, filename, expectedDate, file)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Post created successfully", response.body?.get("message"))
    }

    @Test
    fun `save should return error response when service fails`() {
        // Given
        val version = "1.0.0"
        val file = MockMultipartFile("file", "test.bin", "application/octet-stream", "test data".toByteArray())
        `when`(mappingService.addMapping(any(), any())).thenThrow(RuntimeException("Test error"))

        // When
        val response = articleController.save(version, "", "", "", file)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("Failed to create mapping entry", response.body?.get("error"))
    }

    @Test
    fun `delete should return success response when service succeeds`() {
        // Given
        val version = "1.0.0"

        // When
        val response = articleController.delete(version)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Mapping entry has been removed successfully", response.body?.get("message"))
    }

    @Test
    fun `getPosts should return paginated response`() {
        // Given
        val request = DataTableRequest(
                draw = 1,
                start = 0,
                length = 10,
                search = DataTableRequest.Search(value = "test"),
                order = listOf(DataTableRequest.Order(column = 0, dir = "asc")),
                columns = null
        )

        // When
        val response = articleController.getPosts(request)

        // Then
        assertEquals(1, response.draw)
    }
}