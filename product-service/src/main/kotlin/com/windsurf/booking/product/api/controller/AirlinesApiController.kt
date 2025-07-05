package com.windsurf.booking.product.api.controller

import com.windsurf.booking.product.api.AirlinesApi
import com.windsurf.booking.product.api.model.AirlineResponse
import com.windsurf.booking.product.service.AirlineService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AirlinesApiController(
    private val airlineService: AirlineService
) : AirlinesApi {

    private val logger = LoggerFactory.getLogger(AirlinesApiController::class.java)

    override fun getAirlines(
        query: String?,
        pageNumber: Int?,
        pageSize: Int?
    ): ResponseEntity<AirlineResponse> {
        logger.info("Getting airlines with query: $query")
        
        val airlineResponse = airlineService.getAirlines(
            query = query,
            page = pageNumber ?: 0,
            size = pageSize ?: 20
        )
        
        return ResponseEntity.ok(airlineResponse)
    }
}
