package com.windsurf.booking.product.service.impl

import com.windsurf.booking.product.api.model.Aircraft
import com.windsurf.booking.product.api.model.Flight
import com.windsurf.booking.product.domain.model.Flight as DomainFlight
import com.windsurf.booking.product.domain.model.Stop as DomainStop
import com.windsurf.booking.product.repository.elasticsearch.FlightElasticsearchRepository
import com.windsurf.booking.product.repository.elasticsearch.StopElasticsearchRepository
import com.windsurf.booking.product.service.AirlineService
import com.windsurf.booking.product.service.AirportService
import com.windsurf.booking.product.service.FlightService
import com.windsurf.booking.product.service.mapAirlineEntityToApiModel
import com.windsurf.booking.product.service.mapAirportEntityToApiModel
import com.windsurf.booking.product.service.mapFlightEntityToApiModel
import com.windsurf.booking.product.service.mapStopEntityToApiModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.NoSuchElementException

@Service
class FlightServiceImpl(
    private val flightElasticsearchRepository: FlightElasticsearchRepository,
    private val stopElasticsearchRepository: StopElasticsearchRepository,
    private val airlineService: AirlineService,
    private val airportService: AirportService
) : FlightService {

    private val logger = LoggerFactory.getLogger(FlightServiceImpl::class.java)

    override fun getFlightById(flightId: String): Flight {
        logger.info("Getting flight details for flight ID: $flightId")
        
        // Fetch flight entity from repository
        val flightEntity = flightElasticsearchRepository.findById(flightId)
            .orElseThrow { NoSuchElementException("Flight not found with ID: $flightId") }
        
        // Fetch stops for this flight
        val stopEntities = stopElasticsearchRepository.findByFlightId(flightId)
        
        // Convert to API model using the centralized mapping function
        return mapFlightEntityToApiModel(flightEntity, stopEntities)
    }
    
    override fun createFlight(flight: Flight): Flight {
        logger.info("Creating new flight: ${flight.flightNumber}")
        
        // In a real implementation, we would validate the flight data
        // and convert the API model to a domain entity
        
        // For now, we'll throw an exception as this is not implemented yet
        throw UnsupportedOperationException("Flight creation not implemented yet")
    }
    
    override fun updateFlight(flightId: String, flight: Flight): Flight {
        logger.info("Updating flight with ID: $flightId")
        
        // Check if flight exists
        if (!flightElasticsearchRepository.existsById(flightId)) {
            throw NoSuchElementException("Flight not found with ID: $flightId")
        }
        
        // In a real implementation, we would update the flight entity
        // and convert the API model to a domain entity
        
        // For now, we'll throw an exception as this is not implemented yet
        throw UnsupportedOperationException("Flight update not implemented yet")
    }
    
    override fun deleteFlight(flightId: String) {
        logger.info("Deleting flight with ID: $flightId")
        
        // Check if flight exists
        if (!flightElasticsearchRepository.existsById(flightId)) {
            throw NoSuchElementException("Flight not found with ID: $flightId")
        }
        
        // Delete the flight
        flightElasticsearchRepository.deleteById(flightId)
        
        // Also delete associated stops
        val stops = stopElasticsearchRepository.findByFlightId(flightId)
        stops.forEach { stop -> stopElasticsearchRepository.deleteById(stop.stopId) }
    }
}
