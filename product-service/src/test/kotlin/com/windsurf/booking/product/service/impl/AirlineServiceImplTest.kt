package com.windsurf.booking.product.service.impl

import com.windsurf.booking.product.domain.Airline
import com.windsurf.booking.product.repository.sql.AirlineRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.NoSuchElementException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AirlineServiceImplTest {

    private lateinit var airlineRepository: AirlineRepository
    private lateinit var airlineService: AirlineServiceImpl

    @BeforeEach
    fun setUp() {
        airlineRepository = mockk()
        airlineService = AirlineServiceImpl(airlineRepository)
    }

    @Test
    fun `getAirlines should return airlines with no filters`() {
        // Given
        val page = 0
        val size = 20
        val offset = page * size
        
        val airline1 = Airline(
            airlineCode = "WS",
            name = "Windsurf Airlines",
            logoUrl = "https://example.com/ws.png",
            country = "US",
            active = true,
            allianceCode = "SA",
            allianceName = "Sky Alliance"
        )
        
        val airline2 = Airline(
            airlineCode = "DL",
            name = "Delta Air Lines",
            logoUrl = "https://example.com/dl.png",
            country = "US",
            active = true,
            allianceCode = "ST",
            allianceName = "SkyTeam"
        )
        
        every { airlineRepository.findAll(offset, size) } returns listOf(airline1, airline2)
        every { airlineRepository.count() } returns 2
        
        // When
        val result = airlineService.getAirlines(
            query = null,
            page = page,
            size = size
        )
        
        // Then
        assertNotNull(result)
        assertEquals(2, result.airlines.size)
        assertEquals(2, result.totalResults)
        assertEquals(page, result.pageNumber)
        assertEquals(size, result.pageSize)
        assertEquals("WS", result.airlines[0].airlineCode)
        assertEquals("DL", result.airlines[1].airlineCode)
        
        verify {
            airlineRepository.findAll(offset, size)
            airlineRepository.count()
        }
    }
    
    @Test
    fun `getAirlines should return airlines filtered by query`() {
        // Given
        val query = "Wind"
        val page = 0
        val size = 20
        val offset = page * size
        
        val airline = Airline(
            airlineCode = "WS",
            name = "Windsurf Airlines",
            logoUrl = "https://example.com/ws.png",
            country = "US",
            active = true,
            allianceCode = "SA",
            allianceName = "Sky Alliance"
        )
        
        every { airlineRepository.findByNameOrCode(query, offset, size) } returns listOf(airline)
        every { airlineRepository.countByNameOrCode(query) } returns 1
        
        // When
        val result = airlineService.getAirlines(
            query = query,
            page = page,
            size = size
        )
        
        // Then
        assertNotNull(result)
        assertEquals(1, result.airlines.size)
        assertEquals(1, result.totalResults)
        assertEquals("WS", result.airlines[0].airlineCode)
        
        verify {
            airlineRepository.findByNameOrCode(query, offset, size)
            airlineRepository.countByNameOrCode(query)
        }
    }
    
    @Test
    fun `getAirlineByCode should return airline when found`() {
        // Given
        val code = "WS"
        val airline = Airline(
            airlineCode = code,
            name = "Windsurf Airlines",
            logoUrl = "https://example.com/ws.png",
            country = "US",
            active = true,
            allianceCode = "SA",
            allianceName = "Sky Alliance"
        )
        
        every { airlineRepository.findByCode(code) } returns airline
        
        // When
        val result = airlineService.getAirlineByCode(code)
        
        // Then
        assertNotNull(result)
        assertEquals(code, result.airlineCode)
        assertEquals("Windsurf Airlines", result.name)
        assertEquals("US", result.country)
        assertEquals("SA", result.allianceCode)
        
        verify {
            airlineRepository.findByCode(code)
        }
    }
    
    @Test
    fun `getAirlineByCode should throw exception when airline not found`() {
        // Given
        val code = "INVALID"
        every { airlineRepository.findByCode(code) } returns null
        
        // When/Then
        assertThrows<NoSuchElementException> {
            airlineService.getAirlineByCode(code)
        }
        
        verify {
            airlineRepository.findByCode(code)
        }
    }
    
    @Test
    fun `createAirline should create and return new airline`() {
        // Given
        val airlineModel = com.windsurf.booking.product.api.model.Airline().apply {
            airlineCode = "UA"
            name = "United Airlines"
            logoUrl = "https://example.com/ua.png"
            country = "US"
            active = true
            allianceCode = "SA"
            allianceName = "Star Alliance"
        }
        
        val airlineEntity = Airline(
            airlineCode = "UA",
            name = "United Airlines",
            logoUrl = "https://example.com/ua.png",
            country = "US",
            active = true,
            allianceCode = "SA",
            allianceName = "Star Alliance"
        )
        
        every { airlineRepository.findByCode("UA") } returns null
        every { airlineRepository.save(any()) } returns airlineEntity
        
        // When
        val result = airlineService.createAirline(airlineModel)
        
        // Then
        assertNotNull(result)
        assertEquals("UA", result.airlineCode)
        assertEquals("United Airlines", result.name)
        
        verify {
            airlineRepository.findByCode("UA")
            airlineRepository.save(any())
        }
    }
    
    @Test
    fun `createAirline should throw exception when airline code already exists`() {
        // Given
        val airlineModel = com.windsurf.booking.product.api.model.Airline().apply {
            airlineCode = "WS"
            name = "Windsurf Airlines"
            logoUrl = "https://example.com/ws.png"
            country = "US"
            active = true
            allianceCode = "SA"
            allianceName = "Sky Alliance"
        }
        
        val existingAirline = Airline(
            airlineCode = "WS",
            name = "Windsurf Airlines",
            logoUrl = "https://example.com/ws.png",
            country = "US",
            active = true,
            allianceCode = "SA",
            allianceName = "Sky Alliance"
        )
        
        every { airlineRepository.findByCode("WS") } returns existingAirline
        
        // When/Then
        assertThrows<IllegalArgumentException> {
            airlineService.createAirline(airlineModel)
        }
        
        verify {
            airlineRepository.findByCode("WS")
        }
    }
    
    @Test
    fun `updateAirline should update and return airline`() {
        // Given
        val code = "WS"
        val airlineModel = com.windsurf.booking.product.api.model.Airline().apply {
            airlineCode = code
            name = "Windsurf Airlines International"  // Updated name
            logoUrl = "https://example.com/ws.png"
            country = "US"
            active = true
            allianceCode = "SA"
            allianceName = "Sky Alliance"
        }
        
        val existingAirline = Airline(
            airlineCode = code,
            name = "Windsurf Airlines",  // Original name
            logoUrl = "https://example.com/ws.png",
            country = "US",
            active = true,
            allianceCode = "SA",
            allianceName = "Sky Alliance"
        )
        
        val updatedAirline = Airline(
            airlineCode = code,
            name = "Windsurf Airlines International",  // Updated name
            logoUrl = "https://example.com/ws.png",
            country = "US",
            active = true,
            allianceCode = "SA",
            allianceName = "Sky Alliance"
        )
        
        every { airlineRepository.findByCode(code) } returns existingAirline
        every { airlineRepository.update(any()) } returns updatedAirline
        
        // When
        val result = airlineService.updateAirline(code, airlineModel)
        
        // Then
        assertNotNull(result)
        assertEquals(code, result.airlineCode)
        assertEquals("Windsurf Airlines International", result.name)
        
        verify {
            airlineRepository.findByCode(code)
            airlineRepository.update(any())
        }
    }
    
    @Test
    fun `updateAirline should throw exception when airline not found`() {
        // Given
        val code = "INVALID"
        val airlineModel = com.windsurf.booking.product.api.model.Airline().apply {
            airlineCode = code
            name = "Invalid Airline"
            logoUrl = "https://example.com/invalid.png"
            country = "XX"
            active = false
            allianceCode = null
            allianceName = null
        }
        
        every { airlineRepository.findByCode(code) } returns null
        
        // When/Then
        assertThrows<NoSuchElementException> {
            airlineService.updateAirline(code, airlineModel)
        }
        
        verify {
            airlineRepository.findByCode(code)
        }
    }
    
    @Test
    fun `deleteAirline should delete airline when found`() {
        // Given
        val code = "WS"
        val airline = Airline(
            airlineCode = code,
            name = "Windsurf Airlines",
            logoUrl = "https://example.com/ws.png",
            country = "US",
            active = true,
            allianceCode = "SA",
            allianceName = "Sky Alliance"
        )
        
        every { airlineRepository.findByCode(code) } returns airline
        every { airlineRepository.deleteByCode(code) } returns Unit
        
        // When
        airlineService.deleteAirline(code)
        
        // Then
        verify {
            airlineRepository.findByCode(code)
            airlineRepository.deleteByCode(code)
        }
    }
    
    @Test
    fun `deleteAirline should throw exception when airline not found`() {
        // Given
        val code = "INVALID"
        every { airlineRepository.findByCode(code) } returns null
        
        // When/Then
        assertThrows<NoSuchElementException> {
            airlineService.deleteAirline(code)
        }
        
        verify {
            airlineRepository.findByCode(code)
        }
    }
    
    @Test
    fun `getAirlinesByAlliance should return airlines in the specified alliance`() {
        // Given
        val allianceCode = "SA"
        
        val airline1 = Airline(
            airlineCode = "WS",
            name = "Windsurf Airlines",
            logoUrl = "https://example.com/ws.png",
            country = "US",
            active = true,
            allianceCode = allianceCode,
            allianceName = "Sky Alliance"
        )
        
        val airline2 = Airline(
            airlineCode = "UA",
            name = "United Airlines",
            logoUrl = "https://example.com/ua.png",
            country = "US",
            active = true,
            allianceCode = allianceCode,
            allianceName = "Sky Alliance"
        )
        
        every { airlineRepository.findByAllianceCode(allianceCode) } returns listOf(airline1, airline2)
        
        // When
        val result = airlineService.getAirlinesByAlliance(allianceCode)
        
        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("WS", result[0].airlineCode)
        assertEquals("UA", result[1].airlineCode)
        
        verify {
            airlineRepository.findByAllianceCode(allianceCode)
        }
    }
}
