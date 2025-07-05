package com.windsurf.booking.product.api.controller

import com.windsurf.booking.product.api.SeatsApi
import com.windsurf.booking.product.api.model.SeatResponse
import com.windsurf.booking.product.service.SeatService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SeatsApiController(
    private val seatService: SeatService
) : SeatsApi {

    private val logger = LoggerFactory.getLogger(SeatsApiController::class.java)

    override fun getFlightSeats(flightId: String): ResponseEntity<SeatResponse> {
        logger.info("Getting seats for flight: $flightId")
        
        val seatResponse = seatService.getSeatsForFlight(flightId)
        
        return ResponseEntity.ok(seatResponse)
    }
}
