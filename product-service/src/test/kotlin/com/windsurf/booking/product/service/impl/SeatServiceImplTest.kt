package com.windsurf.booking.product.service.impl

import com.windsurf.booking.product.domain.Seat
import com.windsurf.booking.product.domain.SeatFeature
import com.windsurf.booking.product.domain.Stop
import com.windsurf.booking.product.repository.elasticsearch.FlightElasticsearchRepository
import com.windsurf.booking.product.repository.elasticsearch.SeatElasticsearchRepository
import com.windsurf.booking.product.repository.elasticsearch.StopElasticsearchRepository
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
import kotlin.test.assertTrue

class SeatServiceImplTest {

    private lateinit var seatElasticsearchRepository: SeatElasticsearchRepository
    private lateinit var flightElasticsearchRepository: FlightElasticsearchRepository
    private lateinit var stopElasticsearchRepository: StopElasticsearchRepository
    private lateinit var seatService: SeatServiceImpl

    @BeforeEach
    fun setUp() {
        seatElasticsearchRepository = mockk()
        flightElasticsearchRepository = mockk()
        stopElasticsearchRepository = mockk()
        seatService = SeatServiceImpl(
            seatElasticsearchRepository,
            flightElasticsearchRepository,
            stopElasticsearchRepository
        )
    }

    @Test
    fun `getSeatsForFlight should return seats for all stops of a flight`() {
        // Given
        val flightId = "flight1"
        val stop1Id = "stop1"
        val stop2Id = "stop2"
        
        val now = LocalDateTime.now()
        
        val stop1 = Stop(
            id = stop1Id,
            flightId = flightId,
            departureAirportCode = "JFK",
            arrivalAirportCode = "ORD",
            departureTime = now,
            arrivalTime = now.plusHours(2),
            aircraftId = "aircraft1",
            aircraftModel = "Boeing 737",
            aircraftManufacturer = "Boeing",
            aircraftType = "Narrow-body",
            capacity = 180,
            availableSeats = 150
        )
        
        val stop2 = Stop(
            id = stop2Id,
            flightId = flightId,
            departureAirportCode = "ORD",
            arrivalAirportCode = "LAX",
            departureTime = now.plusHours(3),
            arrivalTime = now.plusHours(6),
            aircraftId = "aircraft1",
            aircraftModel = "Boeing 737",
            aircraftManufacturer = "Boeing",
            aircraftType = "Narrow-body",
            capacity = 180,
            availableSeats = 150
        )
        
        val seat1 = Seat(
            id = "seat1",
            stopId = stop1Id,
            seatNumber = "12A",
            type = "ECONOMY",
            price = 99.99,
            available = true,
            features = setOf(SeatFeature.WINDOW, SeatFeature.EXTRA_LEGROOM)
        )
        
        val seat2 = Seat(
            id = "seat2",
            stopId = stop1Id,
            seatNumber = "12B",
            type = "ECONOMY",
            price = 89.99,
            available = true,
            features = setOf(SeatFeature.MIDDLE)
        )
        
        val seat3 = Seat(
            id = "seat3",
            stopId = stop2Id,
            seatNumber = "12A",
            type = "ECONOMY",
            price = 109.99,
            available = true,
            features = setOf(SeatFeature.WINDOW, SeatFeature.EXTRA_LEGROOM)
        )
        
        every { flightElasticsearchRepository.existsById(flightId) } returns true
        every { stopElasticsearchRepository.findByFlightId(flightId) } returns listOf(stop1, stop2)
        every { seatElasticsearchRepository.findByStopId(stop1Id) } returns listOf(seat1, seat2)
        every { seatElasticsearchRepository.findByStopId(stop2Id) } returns listOf(seat3)
        
        // When
        val result = seatService.getSeatsForFlight(flightId)
        
        // Then
        assertNotNull(result)
        assertEquals(3, result.seats.size)
        assertEquals("seat1", result.seats[0].seatId)
        assertEquals("seat2", result.seats[1].seatId)
        assertEquals("seat3", result.seats[2].seatId)
        assertNotNull(result.seatMap)
        assertEquals(30, result.seatMap.rows)
        assertEquals(6, result.seatMap.columns)
        
        verify {
            flightElasticsearchRepository.existsById(flightId)
            stopElasticsearchRepository.findByFlightId(flightId)
            seatElasticsearchRepository.findByStopId(stop1Id)
            seatElasticsearchRepository.findByStopId(stop2Id)
        }
    }
    
    @Test
    fun `getSeatsForFlight should throw exception when flight not found`() {
        // Given
        val flightId = "nonexistent"
        every { flightElasticsearchRepository.existsById(flightId) } returns false
        
        // When/Then
        assertThrows<NoSuchElementException> {
            seatService.getSeatsForFlight(flightId)
        }
        
        verify {
            flightElasticsearchRepository.existsById(flightId)
        }
    }
    
    @Test
    fun `getSeatById should return seat when found`() {
        // Given
        val seatId = "seat1"
        val seat = Seat(
            id = seatId,
            stopId = "stop1",
            seatNumber = "12A",
            type = "ECONOMY",
            price = 99.99,
            available = true,
            features = setOf(SeatFeature.WINDOW, SeatFeature.EXTRA_LEGROOM)
        )
        
        every { seatElasticsearchRepository.findById(seatId) } returns Optional.of(seat)
        
        // When
        val result = seatService.getSeatById(seatId)
        
        // Then
        assertNotNull(result)
        assertEquals(seatId, result.seatId)
        assertEquals("stop1", result.stopId)
        assertEquals("12A", result.seatNumber)
        assertEquals("ECONOMY", result.type)
        assertEquals(99.99, result.price)
        assertTrue(result.available)
        assertEquals(2, result.features.size)
        assertTrue(result.features.contains("WINDOW"))
        assertTrue(result.features.contains("EXTRA_LEGROOM"))
        
        verify {
            seatElasticsearchRepository.findById(seatId)
        }
    }
    
    @Test
    fun `getSeatById should throw exception when seat not found`() {
        // Given
        val seatId = "nonexistent"
        every { seatElasticsearchRepository.findById(seatId) } returns Optional.empty()
        
        // When/Then
        assertThrows<NoSuchElementException> {
            seatService.getSeatById(seatId)
        }
        
        verify {
            seatElasticsearchRepository.findById(seatId)
        }
    }
    
    @Test
    fun `updateSeatAvailability should update seat availability`() {
        // Given
        val seatId = "seat1"
        val seat = Seat(
            id = seatId,
            stopId = "stop1",
            seatNumber = "12A",
            type = "ECONOMY",
            price = 99.99,
            available = true,
            features = setOf(SeatFeature.WINDOW, SeatFeature.EXTRA_LEGROOM)
        )
        
        val updatedSeat = seat.copy(available = false)
        
        every { seatElasticsearchRepository.findById(seatId) } returns Optional.of(seat)
        every { seatElasticsearchRepository.save(any()) } returns updatedSeat
        
        // When
        val result = seatService.updateSeatAvailability(seatId, false)
        
        // Then
        assertNotNull(result)
        assertEquals(seatId, result.seatId)
        assertEquals(false, result.available)
        
        verify {
            seatElasticsearchRepository.findById(seatId)
            seatElasticsearchRepository.save(any())
        }
    }
    
    @Test
    fun `getAvailableSeatsByClass should return available seats of specified class`() {
        // Given
        val flightId = "flight1"
        val stop1Id = "stop1"
        val stop2Id = "stop2"
        val cabinClass = "ECONOMY"
        
        val stop1 = Stop(
            id = stop1Id,
            flightId = flightId,
            departureAirportCode = "JFK",
            arrivalAirportCode = "ORD",
            departureTime = LocalDateTime.now(),
            arrivalTime = LocalDateTime.now().plusHours(2),
            aircraftId = "aircraft1",
            aircraftModel = "Boeing 737",
            aircraftManufacturer = "Boeing",
            aircraftType = "Narrow-body",
            capacity = 180,
            availableSeats = 150
        )
        
        val stop2 = Stop(
            id = stop2Id,
            flightId = flightId,
            departureAirportCode = "ORD",
            arrivalAirportCode = "LAX",
            departureTime = LocalDateTime.now().plusHours(3),
            arrivalTime = LocalDateTime.now().plusHours(6),
            aircraftId = "aircraft1",
            aircraftModel = "Boeing 737",
            aircraftManufacturer = "Boeing",
            aircraftType = "Narrow-body",
            capacity = 180,
            availableSeats = 150
        )
        
        val seat1 = Seat(
            id = "seat1",
            stopId = stop1Id,
            seatNumber = "12A",
            type = cabinClass,
            price = 99.99,
            available = true,
            features = setOf(SeatFeature.WINDOW, SeatFeature.EXTRA_LEGROOM)
        )
        
        val seat2 = Seat(
            id = "seat2",
            stopId = stop2Id,
            seatNumber = "12A",
            type = cabinClass,
            price = 109.99,
            available = true,
            features = setOf(SeatFeature.WINDOW, SeatFeature.EXTRA_LEGROOM)
        )
        
        every { flightElasticsearchRepository.existsById(flightId) } returns true
        every { stopElasticsearchRepository.findByFlightId(flightId) } returns listOf(stop1, stop2)
        every { seatElasticsearchRepository.findByStopIdAndTypeAndAvailable(stop1Id, cabinClass, true) } returns listOf(seat1)
        every { seatElasticsearchRepository.findByStopIdAndTypeAndAvailable(stop2Id, cabinClass, true) } returns listOf(seat2)
        
        // When
        val result = seatService.getAvailableSeatsByClass(flightId, cabinClass)
        
        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("seat1", result[0].seatId)
        assertEquals("seat2", result[1].seatId)
        
        verify {
            flightElasticsearchRepository.existsById(flightId)
            stopElasticsearchRepository.findByFlightId(flightId)
            seatElasticsearchRepository.findByStopIdAndTypeAndAvailable(stop1Id, cabinClass, true)
            seatElasticsearchRepository.findByStopIdAndTypeAndAvailable(stop2Id, cabinClass, true)
        }
    }
}
