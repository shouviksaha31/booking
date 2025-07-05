package com.windsurf.booking.product.service.impl

import com.windsurf.booking.product.domain.Airport
import com.windsurf.booking.product.repository.sql.AirportRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.NoSuchElementException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AirportServiceImplTest {

    private lateinit var airportRepository: AirportRepository
    private lateinit var airportService: AirportServiceImpl

    @BeforeEach
    fun setUp() {
        airportRepository = mockk()
        airportService = AirportServiceImpl(airportRepository)
    }

    @Test
    fun `getAirports should return airports with no filters`() {
        // Given
        val page = 0
        val size = 20
        val offset = page * size
        
        val airport1 = Airport(
            code = "JFK",
            name = "John F. Kennedy International Airport",
            city = "New York",
            country = "US",
            latitude = 40.6413,
            longitude = -73.7781,
            timezone = "America/New_York"
        )
        
        val airport2 = Airport(
            code = "LAX",
            name = "Los Angeles International Airport",
            city = "Los Angeles",
            country = "US",
            latitude = 33.9416,
            longitude = -118.4085,
            timezone = "America/Los_Angeles"
        )
        
        every { airportRepository.findAll(offset, size) } returns listOf(airport1, airport2)
        every { airportRepository.count() } returns 2
        
        // When
        val result = airportService.getAirports(
            query = null,
            country = null,
            page = page,
            size = size
        )
        
        // Then
        assertNotNull(result)
        assertEquals(2, result.airports.size)
        assertEquals(2, result.totalResults)
        assertEquals(page, result.pageNumber)
        assertEquals(size, result.pageSize)
        assertEquals("JFK", result.airports[0].code)
        assertEquals("LAX", result.airports[1].code)
        
        verify {
            airportRepository.findAll(offset, size)
            airportRepository.count()
        }
    }
    
    @Test
    fun `getAirports should return airports filtered by query`() {
        // Given
        val query = "New York"
        val page = 0
        val size = 20
        val offset = page * size
        
        val airport = Airport(
            code = "JFK",
            name = "John F. Kennedy International Airport",
            city = "New York",
            country = "US",
            latitude = 40.6413,
            longitude = -73.7781,
            timezone = "America/New_York"
        )
        
        every { airportRepository.findByNameOrCode(query, offset, size) } returns listOf(airport)
        every { airportRepository.countByNameOrCode(query) } returns 1
        
        // When
        val result = airportService.getAirports(
            query = query,
            country = null,
            page = page,
            size = size
        )
        
        // Then
        assertNotNull(result)
        assertEquals(1, result.airports.size)
        assertEquals(1, result.totalResults)
        assertEquals("JFK", result.airports[0].code)
        
        verify {
            airportRepository.findByNameOrCode(query, offset, size)
            airportRepository.countByNameOrCode(query)
        }
    }
    
    @Test
    fun `getAirports should return airports filtered by country`() {
        // Given
        val country = "US"
        val page = 0
        val size = 20
        val offset = page * size
        
        val airport1 = Airport(
            code = "JFK",
            name = "John F. Kennedy International Airport",
            city = "New York",
            country = "US",
            latitude = 40.6413,
            longitude = -73.7781,
            timezone = "America/New_York"
        )
        
        val airport2 = Airport(
            code = "LAX",
            name = "Los Angeles International Airport",
            city = "Los Angeles",
            country = "US",
            latitude = 33.9416,
            longitude = -118.4085,
            timezone = "America/Los_Angeles"
        )
        
        every { airportRepository.findByCountry(country, offset, size) } returns listOf(airport1, airport2)
        every { airportRepository.countByCountry(country) } returns 2
        
        // When
        val result = airportService.getAirports(
            query = null,
            country = country,
            page = page,
            size = size
        )
        
        // Then
        assertNotNull(result)
        assertEquals(2, result.airports.size)
        assertEquals(2, result.totalResults)
        assertEquals("JFK", result.airports[0].code)
        assertEquals("LAX", result.airports[1].code)
        
        verify {
            airportRepository.findByCountry(country, offset, size)
            airportRepository.countByCountry(country)
        }
    }
    
    @Test
    fun `getAirports should return airports filtered by query and country`() {
        // Given
        val query = "New"
        val country = "US"
        val page = 0
        val size = 20
        val offset = page * size
        
        val airport = Airport(
            code = "JFK",
            name = "John F. Kennedy International Airport",
            city = "New York",
            country = "US",
            latitude = 40.6413,
            longitude = -73.7781,
            timezone = "America/New_York"
        )
        
        every { airportRepository.findByNameOrCodeAndCountry(query, country, offset, size) } returns listOf(airport)
        every { airportRepository.countByNameOrCodeAndCountry(query, country) } returns 1
        
        // When
        val result = airportService.getAirports(
            query = query,
            country = country,
            page = page,
            size = size
        )
        
        // Then
        assertNotNull(result)
        assertEquals(1, result.airports.size)
        assertEquals(1, result.totalResults)
        assertEquals("JFK", result.airports[0].code)
        
        verify {
            airportRepository.findByNameOrCodeAndCountry(query, country, offset, size)
            airportRepository.countByNameOrCodeAndCountry(query, country)
        }
    }
    
    @Test
    fun `getAirportByCode should return airport when found`() {
        // Given
        val code = "JFK"
        val airport = Airport(
            code = code,
            name = "John F. Kennedy International Airport",
            city = "New York",
            country = "US",
            latitude = 40.6413,
            longitude = -73.7781,
            timezone = "America/New_York"
        )
        
        every { airportRepository.findByCode(code) } returns airport
        
        // When
        val result = airportService.getAirportByCode(code)
        
        // Then
        assertNotNull(result)
        assertEquals(code, result.code)
        assertEquals("John F. Kennedy International Airport", result.name)
        assertEquals("New York", result.city)
        assertEquals("US", result.country)
        
        verify {
            airportRepository.findByCode(code)
        }
    }
    
    @Test
    fun `getAirportByCode should throw exception when airport not found`() {
        // Given
        val code = "INVALID"
        every { airportRepository.findByCode(code) } returns null
        
        // When/Then
        assertThrows<NoSuchElementException> {
            airportService.getAirportByCode(code)
        }
        
        verify {
            airportRepository.findByCode(code)
        }
    }
    
    @Test
    fun `createAirport should create and return new airport`() {
        // Given
        val airportModel = com.windsurf.booking.product.api.model.Airport().apply {
            code = "SFO"
            name = "San Francisco International Airport"
            city = "San Francisco"
            country = "US"
            latitude = 37.6213
            longitude = -122.3790
            timezone = "America/Los_Angeles"
        }
        
        val airportEntity = Airport(
            code = "SFO",
            name = "San Francisco International Airport",
            city = "San Francisco",
            country = "US",
            latitude = 37.6213,
            longitude = -122.3790,
            timezone = "America/Los_Angeles"
        )
        
        every { airportRepository.findByCode("SFO") } returns null
        every { airportRepository.save(any()) } returns airportEntity
        
        // When
        val result = airportService.createAirport(airportModel)
        
        // Then
        assertNotNull(result)
        assertEquals("SFO", result.code)
        assertEquals("San Francisco International Airport", result.name)
        
        verify {
            airportRepository.findByCode("SFO")
            airportRepository.save(any())
        }
    }
    
    @Test
    fun `createAirport should throw exception when airport code already exists`() {
        // Given
        val airportModel = com.windsurf.booking.product.api.model.Airport().apply {
            code = "JFK"
            name = "John F. Kennedy International Airport"
            city = "New York"
            country = "US"
            latitude = 40.6413
            longitude = -73.7781
            timezone = "America/New_York"
        }
        
        val existingAirport = Airport(
            code = "JFK",
            name = "John F. Kennedy International Airport",
            city = "New York",
            country = "US",
            latitude = 40.6413,
            longitude = -73.7781,
            timezone = "America/New_York"
        )
        
        every { airportRepository.findByCode("JFK") } returns existingAirport
        
        // When/Then
        assertThrows<IllegalArgumentException> {
            airportService.createAirport(airportModel)
        }
        
        verify {
            airportRepository.findByCode("JFK")
        }
    }
    
    @Test
    fun `updateAirport should update and return airport`() {
        // Given
        val code = "JFK"
        val airportModel = com.windsurf.booking.product.api.model.Airport().apply {
            this.code = code
            name = "John F. Kennedy International Airport"
            city = "New York"
            country = "US"
            latitude = 40.6413
            longitude = -73.7781
            timezone = "America/New_York"
        }
        
        val existingAirport = Airport(
            code = code,
            name = "JFK International Airport", // Different name
            city = "New York",
            country = "US",
            latitude = 40.6413,
            longitude = -73.7781,
            timezone = "America/New_York"
        )
        
        val updatedAirport = Airport(
            code = code,
            name = "John F. Kennedy International Airport",
            city = "New York",
            country = "US",
            latitude = 40.6413,
            longitude = -73.7781,
            timezone = "America/New_York"
        )
        
        every { airportRepository.findByCode(code) } returns existingAirport
        every { airportRepository.update(any()) } returns updatedAirport
        
        // When
        val result = airportService.updateAirport(code, airportModel)
        
        // Then
        assertNotNull(result)
        assertEquals(code, result.code)
        assertEquals("John F. Kennedy International Airport", result.name)
        
        verify {
            airportRepository.findByCode(code)
            airportRepository.update(any())
        }
    }
    
    @Test
    fun `updateAirport should throw exception when airport not found`() {
        // Given
        val code = "INVALID"
        val airportModel = com.windsurf.booking.product.api.model.Airport().apply {
            this.code = code
            name = "Invalid Airport"
            city = "Nowhere"
            country = "XX"
            latitude = 0.0
            longitude = 0.0
            timezone = "UTC"
        }
        
        every { airportRepository.findByCode(code) } returns null
        
        // When/Then
        assertThrows<NoSuchElementException> {
            airportService.updateAirport(code, airportModel)
        }
        
        verify {
            airportRepository.findByCode(code)
        }
    }
    
    @Test
    fun `deleteAirport should delete airport when found`() {
        // Given
        val code = "JFK"
        val airport = Airport(
            code = code,
            name = "John F. Kennedy International Airport",
            city = "New York",
            country = "US",
            latitude = 40.6413,
            longitude = -73.7781,
            timezone = "America/New_York"
        )
        
        every { airportRepository.findByCode(code) } returns airport
        every { airportRepository.deleteByCode(code) } returns Unit
        
        // When
        airportService.deleteAirport(code)
        
        // Then
        verify {
            airportRepository.findByCode(code)
            airportRepository.deleteByCode(code)
        }
    }
    
    @Test
    fun `deleteAirport should throw exception when airport not found`() {
        // Given
        val code = "INVALID"
        every { airportRepository.findByCode(code) } returns null
        
        // When/Then
        assertThrows<NoSuchElementException> {
            airportService.deleteAirport(code)
        }
        
        verify {
            airportRepository.findByCode(code)
        }
    }
}
