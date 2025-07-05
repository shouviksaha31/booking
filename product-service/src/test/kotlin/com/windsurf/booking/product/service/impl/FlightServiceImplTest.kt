package com.windsurf.booking.product.service.impl

import com.windsurf.booking.product.api.model.Airline
import com.windsurf.booking.product.api.model.Airport
import com.windsurf.booking.product.domain.Flight
import com.windsurf.booking.product.domain.FlightStatus
import com.windsurf.booking.product.domain.Stop
import com.windsurf.booking.product.repository.elasticsearch.FlightElasticsearchRepository
import com.windsurf.booking.product.repository.elasticsearch.StopElasticsearchRepository
import com.windsurf.booking.product.service.AirlineService
import com.windsurf.booking.product.service.AirportService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.NoSuchElementException
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FlightServiceImplTest {

    private lateinit var flightElasticsearchRepository: FlightElasticsearchRepository
    private lateinit var stopElasticsearchRepository: StopElasticsearchRepository
    private lateinit var airlineService: AirlineService
    private lateinit var airportService: AirportService
    private lateinit var flightService: FlightServiceImpl

    @BeforeEach
    fun setUp() {
        flightElasticsearchRepository = mockk()
        stopElasticsearchRepository = mockk()
        airlineService = mockk()
        airportService = mockk()
        flightService = FlightServiceImpl(
            flightElasticsearchRepository,
            stopElasticsearchRepository,
            airlineService,
            airportService
        )
    }

    @Test
    fun `getFlightById should return flight when found`() {
        // Given
        val flightId = "flight1"
        val departureTime = LocalDateTime.now()
        val arrivalTime = departureTime.plusHours(3)
        
        val flight = Flight(
            id = flightId,
            flightNumber = "WS123",
            airlineCode = "WS",
            originAirportCode = "JFK",
            destinationAirportCode = "LAX",
            departureTime = departureTime,
            arrivalTime = arrivalTime,
            status = FlightStatus.SCHEDULED,
            aircraftId = "aircraft1",
            aircraftModel = "Boeing 737",
            aircraftManufacturer = "Boeing",
            aircraftType = "Narrow-body",
            capacity = 180,
            priceMap = mapOf("ECONOMY" to 299.99, "BUSINESS" to 899.99)
        )
        
        val stop = Stop(
            id = "stop1",
            flightId = flightId,
            departureAirportCode = "JFK",
            arrivalAirportCode = "LAX",
            departureTime = departureTime,
            arrivalTime = arrivalTime,
            aircraftId = "aircraft1",
            aircraftModel = "Boeing 737",
            aircraftManufacturer = "Boeing",
            aircraftType = "Narrow-body",
            capacity = 180,
            availableSeats = 150
        )
        
        val airline = Airline().apply {
            airlineCode = "WS"
            name = "Windsurf Airlines"
            logoUrl = "https://example.com/logo.png"
            country = "US"
            active = true
            allianceCode = "SA"
            allianceName = "Sky Alliance"
        }
        
        val originAirport = Airport().apply {
            code = "JFK"
            name = "John F. Kennedy International Airport"
            city = "New York"
            country = "US"
            latitude = 40.6413
            longitude = -73.7781
            timezone = "America/New_York"
        }
        
        val destinationAirport = Airport().apply {
            code = "LAX"
            name = "Los Angeles International Airport"
            city = "Los Angeles"
            country = "US"
            latitude = 33.9416
            longitude = -118.4085
            timezone = "America/Los_Angeles"
        }
        
        every { flightElasticsearchRepository.findById(flightId) } returns Optional.of(flight)
        every { stopElasticsearchRepository.findByFlightId(flightId) } returns listOf(stop)
        every { airlineService.getAirlineByCode("WS") } returns airline
        every { airportService.getAirportByCode("JFK") } returns originAirport
        every { airportService.getAirportByCode("LAX") } returns destinationAirport
        
        // When
        val result = flightService.getFlightById(flightId)
        
        // Then
        assertNotNull(result)
        assertEquals(flightId, result.id)
        assertEquals("WS123", result.flightNumber)
        assertEquals("WS", result.airline.airlineCode)
        assertEquals("JFK", result.origin.code)
        assertEquals("LAX", result.destination.code)
        assertEquals(departureTime.toString(), result.departureTime)
        assertEquals(arrivalTime.toString(), result.arrivalTime)
        assertEquals(180, result.duration)
        assertEquals(1, result.stops.size)
        assertEquals("stop1", result.stops[0].stopId)
        assertEquals("aircraft1", result.aircraft.aircraftId)
        assertEquals("SCHEDULED", result.status)
        
        verify {
            flightElasticsearchRepository.findById(flightId)
            stopElasticsearchRepository.findByFlightId(flightId)
            airlineService.getAirlineByCode("WS")
            airportService.getAirportByCode("JFK")
            airportService.getAirportByCode("LAX")
        }
    }
    
    @Test
    fun `getFlightById should throw exception when flight not found`() {
        // Given
        val flightId = "nonexistent"
        every { flightElasticsearchRepository.findById(flightId) } returns Optional.empty()
        
        // When/Then
        assertThrows<NoSuchElementException> {
            flightService.getFlightById(flightId)
        }
        
        verify {
            flightElasticsearchRepository.findById(flightId)
        }
    }
    
    @Test
    fun `deleteFlight should delete flight and associated stops`() {
        // Given
        val flightId = "flight1"
        every { flightElasticsearchRepository.existsById(flightId) } returns true
        every { flightElasticsearchRepository.deleteById(flightId) } returns Unit
        every { stopElasticsearchRepository.deleteByFlightId(flightId) } returns Unit
        
        // When
        flightService.deleteFlight(flightId)
        
        // Then
        verify {
            flightElasticsearchRepository.existsById(flightId)
            flightElasticsearchRepository.deleteById(flightId)
            stopElasticsearchRepository.deleteByFlightId(flightId)
        }
    }
    
    @Test
    fun `deleteFlight should throw exception when flight not found`() {
        // Given
        val flightId = "nonexistent"
        every { flightElasticsearchRepository.existsById(flightId) } returns false
        
        // When/Then
        assertThrows<NoSuchElementException> {
            flightService.deleteFlight(flightId)
        }
        
        verify {
            flightElasticsearchRepository.existsById(flightId)
        }
    }
}
