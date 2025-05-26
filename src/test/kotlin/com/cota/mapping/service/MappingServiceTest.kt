package com.cota.mapping.service

import com.cota.mapping.entity.DataTableRequest
import com.cota.mapping.entity.MappingEntry
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.client.RestTemplate


@ExtendWith(MockitoExtension::class)
class MappingServiceTest {

    @Mock
    private lateinit var restTemplate: RestTemplate

    @InjectMocks
    private lateinit var mappingService: MappingService

    @Test
    fun `fetchMappings should return empty list when API fails`() {
        // Given
        `when`(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any<HttpEntity<*>>(),
                any<ParameterizedTypeReference<List<MappingEntry>>>()
        )).thenThrow(RuntimeException("API error"))

        // When
        val result = mappingService.fetchMappings()

        // Then
        assertNotNull(result.error)
        assertNull(result.data)
    }

    @Test
    fun `getPaginatedMappings should return filtered and sorted results`() {
        // When
        val request = DataTableRequest(
                draw = 1,
                start = 0,
                length = 10,
                search = DataTableRequest.Search(value = "1.1.0"),
                order = listOf(DataTableRequest.Order(column = 0, dir = "asc")),
                columns = null
        )
        val response = mappingService.getPaginatedMappings(request)

        // Then
        assertEquals(1, response.draw)
        assertEquals(2, response.recordsTotal)
        assertEquals(1, response.recordsFiltered)
        assertEquals(1, response.data?.size)
        assertEquals("1.1.0", response.data?.first()?.appVersion)
    }

    @Test
    fun `getByVersion should return mapping entry when found`() {
        // Given
        val version = "1.1.0"
        val expectedEntry = MappingEntry(appVersion = version, filename = "test.bin", comments = "", dateUploaded = "")
        `when`(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any<HttpEntity<*>>(),
                eq(MappingEntry::class.java)
        )).thenReturn(ResponseEntity(expectedEntry, HttpStatus.OK))

        // When
        val result = mappingService.getByVersion(version)

        // Then
        assertEquals(version, result.data?.appVersion)
        assertNull(result.error)
    }

    @Test
    fun `addMapping should return success when upload and API call succeed`() {
        // Given
        val info = MappingEntry(appVersion = "1.0.0", filename = "test.bin", comments = "", dateUploaded = "")
        `when`(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any<HttpEntity<*>>(),
                eq(String::class.java)
        )).thenReturn(ResponseEntity("success", HttpStatus.OK))

        // When
        val result = mappingService.addMapping(info, null)

        // Then
        assertEquals("success", result.data)
        assertNull(result.error)
    }

    @Test
    fun `deleteMapping should return success when API call succeeds`() {
        // Given
        val version = "1.0.0"
        `when`(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any<HttpEntity<*>>(),
                eq(String::class.java)
        )).thenReturn(ResponseEntity("deleted", HttpStatus.OK))

        // When
        val result = mappingService.deleteMapping(version)

        // Then
        assertEquals("deleted", result.data)
        assertNull(result.error)
    }

    @Test
    fun `getHttpHeaders should return headers with required values`() {
        // When
        val headers = mappingService.getHttpHeaders()

        // Then
        assertEquals("application/json", headers[HttpHeaders.CONTENT_TYPE]?.first())
        assertTrue(headers[Constants.HeaderKeys.TRACE_ID]?.first()?.isNotBlank() == true)
        assertTrue(headers[Constants.HeaderKeys.ACCOUNT_ID]?.first()?.isNotBlank() == true)
        assertTrue(headers[Constants.HeaderKeys.SOFTWARE_ID]?.first()?.isNotBlank() == true)
    }
}