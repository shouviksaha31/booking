package com.windsurf.booking.product.service.impl

import com.windsurf.booking.product.domain.Flight
import com.windsurf.booking.product.domain.FlightStatus
import com.windsurf.booking.product.repository.elasticsearch.FlightElasticsearchRepository
import com.windsurf.booking.product.repository.elasticsearch.FlightSearchRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FlightSearchServiceImplTest {

    private lateinit var flightSearchRepository: FlightSearchRepository
    private lateinit var flightElasticsearchRepository: FlightElasticsearchRepository
    private lateinit var flightSearchService: FlightSearchServiceImpl

    @BeforeEach
    fun setUp() {
        flightSearchRepository = mockk()
        flightElasticsearchRepository = mockk()
        flightSearchService = FlightSearchServiceImpl(flightSearchRepository, flightElasticsearchRepository)
    }

    @Test
    fun `searchFlights should return flight search response with flights`() {
        // Given
        val origin = "JFK"
        val destination = "LAX"
        val departureDate = LocalDate.now()
        val departureStartDateTime = LocalDateTime.of(departureDate, LocalTime.MIN)
        val departureEndDateTime = LocalDateTime.of(departureDate, LocalTime.MAX)
        val passengers = 2
        val cabinClass = "ECONOMY"
        val page = 0
        val size = 20
        val pageable = PageRequest.of(page, size)

        val flight1 = Flight(
            id = "flight1",
            flightNumber = "WS123",
            airlineCode = "WS",
            originAirportCode = origin,
            destinationAirportCode = destination,
            departureTime = departureStartDateTime.plusHours(2),
            arrivalTime = departureStartDateTime.plusHours(5),
            status = FlightStatus.SCHEDULED,
            aircraftId = "aircraft1",
            aircraftModel = "Boeing 737",
            aircraftManufacturer = "Boeing",
            aircraftType = "Narrow-body",
            capacity = 180,
            priceMap = mapOf("ECONOMY" to 299.99, "BUSINESS" to 899.99)
        )

        val flight2 = Flight(
            id = "flight2",
            flightNumber = "WS456",
            airlineCode = "WS",
            originAirportCode = origin,
            destinationAirportCode = destination,
            departureTime = departureStartDateTime.plusHours(4),
            arrivalTime = departureStartDateTime.plusHours(7),
            status = FlightStatus.SCHEDULED,
            aircraftId = "aircraft2",
            aircraftModel = "Airbus A320",
            aircraftManufacturer = "Airbus",
            aircraftType = "Narrow-body",
            capacity = 150,
            priceMap = mapOf("ECONOMY" to 349.99, "BUSINESS" to 999.99)
        )

        val flightPage = PageImpl(listOf(flight1, flight2), pageable, 2)

        every {
            flightSearchRepository.searchFlights(
                originAirportCode = origin,
                destinationAirportCode = destination,
                departureStartDateTime = departureStartDateTime,
                departureEndDateTime = departureEndDateTime,
                cabinClass = cabinClass,
                minAvailableSeats = passengers,
                pageable = pageable
            )
        } returns flightPage

        // When
        val result = flightSearchService.searchFlights(
            origin = origin,
            destination = destination,
            departureDate = departureDate,
            passengers = passengers,
            cabinClass = cabinClass,
            page = page,
            size = size
        )

        // Then
        assertNotNull(result)
        assertEquals(2, result.flights.size)
        assertEquals(2, result.totalResults)
        assertEquals(page, result.pageNumber)
        assertEquals(size, result.pageSize)
        assertEquals("flight1", result.flights[0].id)
        assertEquals("flight2", result.flights[1].id)

        verify {
            flightSearchRepository.searchFlights(
                originAirportCode = origin,
                destinationAirportCode = destination,
                departureStartDateTime = departureStartDateTime,
                departureEndDateTime = departureEndDateTime,
                cabinClass = cabinClass,
                minAvailableSeats = passengers,
                pageable = pageable
            )
        }
    }

    @Test
    fun `searchFlightsWithFlexibleDates should return map of dates to search responses`() {
        // Given
        val origin = "JFK"
        val destination = "LAX"
        val departureDate = LocalDate.now()
        val passengers = 2
        val cabinClass = "ECONOMY"

        // Mock searchFlights to return a simple response for each date
        for (i in -3..3) {
            val currentDate = departureDate.plusDays(i.toLong())
            val departureStartDateTime = LocalDateTime.of(currentDate, LocalTime.MIN)
            val departureEndDateTime = LocalDateTime.of(currentDate, LocalTime.MAX)
            val pageable = PageRequest.of(0, 5)

            val flight = Flight(
                id = "flight$i",
                flightNumber = "WS$i",
                airlineCode = "WS",
                originAirportCode = origin,
                destinationAirportCode = destination,
                departureTime = departureStartDateTime.plusHours(2),
                arrivalTime = departureStartDateTime.plusHours(5),
                status = FlightStatus.SCHEDULED,
                aircraftId = "aircraft$i",
                aircraftModel = "Boeing 737",
                aircraftManufacturer = "Boeing",
                aircraftType = "Narrow-body",
                capacity = 180,
                priceMap = mapOf("ECONOMY" to 299.99, "BUSINESS" to 899.99)
            )

            val flightPage = PageImpl(listOf(flight), pageable, 1)

            every {
                flightSearchRepository.searchFlights(
                    originAirportCode = origin,
                    destinationAirportCode = destination,
                    departureStartDateTime = departureStartDateTime,
                    departureEndDateTime = departureEndDateTime,
                    cabinClass = cabinClass,
                    minAvailableSeats = passengers,
                    pageable = pageable
                )
            } returns flightPage
        }

        // When
        val result = flightSearchService.searchFlightsWithFlexibleDates(
            origin = origin,
            destination = destination,
            departureDate = departureDate,
            passengers = passengers,
            cabinClass = cabinClass
        )

        // Then
        assertNotNull(result)
        assertEquals(7, result.size)
        
        // Verify each date has a response
        for (i in -3..3) {
            val currentDate = departureDate.plusDays(i.toLong())
            val response = result[currentDate]
            assertNotNull(response)
            assertEquals(1, response.flights.size)
            assertEquals("flight$i", response.flights[0].id)
        }
    }
}
