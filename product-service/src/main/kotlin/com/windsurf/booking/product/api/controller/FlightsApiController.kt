package com.windsurf.booking.product.api.controller

import com.windsurf.booking.product.api.FlightsApi
import com.windsurf.booking.product.api.model.Flight
import com.windsurf.booking.product.api.model.FlightSearchResponse
import com.windsurf.booking.product.api.model.SeatResponse
import com.windsurf.booking.product.service.FlightSearchService
import com.windsurf.booking.product.service.FlightService
import com.windsurf.booking.product.service.SeatService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class FlightsApiController(
    private val flightSearchService: FlightSearchService,
    private val flightService: FlightService,
    private val seatService: SeatService
) : FlightsApi {

    private val logger = LoggerFactory.getLogger(FlightsApiController::class.java)

    override fun searchFlights(
        origin: String,
        destination: String,
        departureDate: LocalDate,
        returnDate: LocalDate?,
        passengers: Int,
        cabinClass: String?,
        pageNumber: Int?,
        pageSize: Int?
    ): ResponseEntity<FlightSearchResponse> {
        logger.info("Searching flights from $origin to $destination on $departureDate")
        
        val searchResponse = flightSearchService.searchFlights(
            origin = origin,
            destination = destination,
            departureDate = departureDate,
            returnDate = returnDate,
            passengers = passengers,
            cabinClass = cabinClass ?: "ECONOMY",
            page = pageNumber ?: 0,
            size = pageSize ?: 20
        )
        
        return ResponseEntity.ok(searchResponse)
    }

    override fun getFlightDetails(flightId: String): ResponseEntity<Flight> {
        logger.info("Getting details for flight $flightId")
        
        val flight = flightService.getFlightById(flightId)
        return ResponseEntity.ok(flight)
    }

    override fun getFlightSeats(flightId: String): ResponseEntity<SeatResponse> {
        logger.info("Getting seats for flight $flightId")
        
        val seatResponse = seatService.getSeatsForFlight(flightId)
        return ResponseEntity.ok(seatResponse)
    }
}
