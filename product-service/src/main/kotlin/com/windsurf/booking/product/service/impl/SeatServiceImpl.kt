package com.windsurf.booking.product.service.impl

import com.windsurf.booking.product.api.model.Seat
import com.windsurf.booking.product.api.model.SeatMap
import com.windsurf.booking.product.api.model.SeatResponse
import com.windsurf.booking.product.domain.model.Seat as DomainSeat
import com.windsurf.booking.product.domain.model.SeatFeature
import com.windsurf.booking.product.domain.model.SeatType
import com.windsurf.booking.product.repository.elasticsearch.FlightElasticsearchRepository
import com.windsurf.booking.product.repository.elasticsearch.SeatElasticsearchRepository
import com.windsurf.booking.product.repository.elasticsearch.StopElasticsearchRepository
import com.windsurf.booking.product.service.SeatService
import com.windsurf.booking.product.service.mapSeatEntityToApiModel
import com.windsurf.booking.product.service.generateSeatMap
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.NoSuchElementException

@Service
class SeatServiceImpl(
    private val seatElasticsearchRepository: SeatElasticsearchRepository,
    private val flightElasticsearchRepository: FlightElasticsearchRepository,
    private val stopElasticsearchRepository: StopElasticsearchRepository
) : SeatService {

    private val logger = LoggerFactory.getLogger(SeatServiceImpl::class.java)

    override fun getSeatsForFlight(flightId: String): SeatResponse {
        logger.info("Getting seats for flight ID: $flightId")
        
        // Check if flight exists
        if (!flightElasticsearchRepository.existsById(flightId)) {
            throw NoSuchElementException("Flight not found with ID: $flightId")
        }
        
        // Get all stops for this flight
        val stops = stopElasticsearchRepository.findByFlightId(flightId)
        
        // Get all seats for all stops
        val seatEntities = stops.flatMap { stop ->
            seatElasticsearchRepository.findByStopId(stop.stopId)
        }
        
        // Convert to API models
        val seats = seatEntities.map { seatEntity ->
            mapSeatEntityToApiModel(seatEntity)
        }
        
        // Generate seat map
        val seatMap = generateSeatMap(seats)
        
        // Create and return response using constructor
        return SeatResponse(
            seats = seats,
            seatMap = seatMap
        )
    }
    
    override fun getSeatById(seatId: String): Seat {
        logger.info("Getting seat with ID: $seatId")
        
        val seatEntity = seatElasticsearchRepository.findById(seatId)
            .orElseThrow { NoSuchElementException("Seat not found with ID: $seatId") }
        
        return mapSeatEntityToApiModel(seatEntity)
    }
    
    override fun updateSeatAvailability(seatId: String, available: Boolean): Seat {
        logger.info("Updating availability for seat ID: $seatId to $available")
        
        val seatEntity = seatElasticsearchRepository.findById(seatId)
            .orElseThrow { NoSuchElementException("Seat not found with ID: $seatId") }
        
        // Create a copy with updated availability
        val updatedEntity = seatEntity.copy(available = available)
        
        // Save updated entity
        val savedEntity = seatElasticsearchRepository.save(updatedEntity)
        
        return mapSeatEntityToApiModel(savedEntity)
    }
    
    override fun getAvailableSeatsByClass(flightId: String, cabinClass: String): List<Seat> {
        logger.info("Getting available $cabinClass seats for flight ID: $flightId")
        
        // Check if flight exists
        if (!flightElasticsearchRepository.existsById(flightId)) {
            throw NoSuchElementException("Flight not found with ID: $flightId")
        }
        
        // Get all stops for this flight
        val stops = stopElasticsearchRepository.findByFlightId(flightId)
        
        // Get all available seats of the specified class for all stops
        val seatEntities = stops.flatMap { stop ->
            seatElasticsearchRepository.findByStopIdAndTypeAndAvailable(
                stopId = stop.stopId,
                type = SeatType.valueOf(cabinClass),
                available = true
            )
        }
        
        // Convert to API models
        return seatEntities.map { seatEntity ->
            mapSeatEntityToApiModel(seatEntity)
        }
    }
}
