package com.windsurf.booking.product.api.controller

import com.windsurf.booking.product.api.AirportsApi
import com.windsurf.booking.product.api.model.AirportResponse
import com.windsurf.booking.product.service.AirportService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AirportsApiController(
    private val airportService: AirportService
) : AirportsApi {

    private val logger = LoggerFactory.getLogger(AirportsApiController::class.java)

    override fun getAirports(
        query: String?,
        country: String?,
        pageNumber: Int,
        pageSize: Int
    ): ResponseEntity<AirportResponse> {
        logger.info("Getting airports with query: $query, country: $country, page: $pageNumber, size: $pageSize")
        
        val airportResponse = airportService.getAirports(
            query = query,
            country = country,
            page = pageNumber,
            size = pageSize
        )
        
        return ResponseEntity.ok(airportResponse)
    }
}
