package com.windsurf.booking.product.service.impl

import com.windsurf.booking.product.api.model.Airline
import com.windsurf.booking.product.api.model.Airport
import com.windsurf.booking.product.api.model.Flight
import com.windsurf.booking.product.api.model.FlightSearchResponse
import com.windsurf.booking.product.domain.model.FlightSearchCriteria
import com.windsurf.booking.product.domain.model.PassengerType
import com.windsurf.booking.product.domain.model.SeatType
import com.windsurf.booking.product.domain.model.Stop
import com.windsurf.booking.product.repository.elasticsearch.FlightElasticsearchRepository
import com.windsurf.booking.product.repository.elasticsearch.FlightSearchRepository
import com.windsurf.booking.product.service.FlightSearchService
import com.windsurf.booking.product.service.mapFlightEntityToApiModel
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.OffsetDateTime
import java.time.ZoneId

@Service
class FlightSearchServiceImpl(
    private val flightSearchRepository: FlightSearchRepository,
    private val flightElasticsearchRepository: FlightElasticsearchRepository
) : FlightSearchService {

    private val logger = LoggerFactory.getLogger(FlightSearchServiceImpl::class.java)

    override fun searchFlights(
        origin: String,
        destination: String,
        departureDate: LocalDate,
        returnDate: LocalDate?,
        passengers: Int,
        cabinClass: String,
        page: Int,
        size: Int
    ): FlightSearchResponse {
        logger.info("Searching flights from $origin to $destination on $departureDate")
        
        // Create search criteria
        val searchCriteria = FlightSearchCriteria(
            origin = origin,
            destination = destination,
            departureDate = departureDate,
            returnDate = returnDate,
            passengers = mapOf(PassengerType.ADULT to passengers),
            preferredCabinClass = try { SeatType.valueOf(cabinClass) } catch (e: Exception) { null },
            page = page,
            size = size
        )
        
        // Search for flights
        val flightEntities = flightSearchRepository.searchFlights(searchCriteria)
        
        // Convert domain entities to API models
        val flights = flightEntities.map { flightEntity ->
            mapFlightEntityToApiModel(flightEntity)
        }
        
        // Create and return response using constructor
        return FlightSearchResponse(
            flights = flights,
            totalResults = flightEntities.size,
            pageNumber = page,
            pageSize = size
        )
    }
    
    override fun searchFlightsWithFlexibleDates(
        origin: String,
        destination: String,
        departureDate: LocalDate,
        returnDate: LocalDate?,
        passengers: Int,
        cabinClass: String
    ): Map<LocalDate, FlightSearchResponse> {
        logger.info("Searching flights with flexible dates from $origin to $destination around $departureDate")
        
        // Create search criteria with flexible dates enabled
        val searchCriteria = FlightSearchCriteria(
            origin = origin,
            destination = destination,
            departureDate = departureDate,
            returnDate = returnDate,
            passengers = mapOf(PassengerType.ADULT to passengers),
            preferredCabinClass = try { SeatType.valueOf(cabinClass) } catch (e: Exception) { null },
            flexibleDates = true,
            page = 0,
            size = 5 // Limit results per day for flexible search
        )
        
        // Get flexible date results
        val flexibleResults = flightSearchRepository.searchFlexibleDates(searchCriteria)
        
        // Convert to map of FlightSearchResponse objects
        return flexibleResults.mapValues { (_, flights) ->
            // Convert domain entities to API models
            val apiFlights = flights.map { flightEntity ->
                mapFlightEntityToApiModel(flightEntity)
            }
            
            // Create response for this date
            FlightSearchResponse(
                flights = apiFlights,
                totalResults = apiFlights.size,
                pageNumber = 0,
                pageSize = apiFlights.size
            )
        }
    }
}
