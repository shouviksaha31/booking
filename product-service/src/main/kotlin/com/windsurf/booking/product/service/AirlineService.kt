package com.windsurf.booking.product.service

import com.windsurf.booking.product.api.model.Airline
import com.windsurf.booking.product.api.model.AirlineResponse

/**
 * Service for managing airline information
 */
interface AirlineService {
    /**
     * Get airlines with optional filtering
     */
    fun getAirlines(
        query: String? = null,
        page: Int = 0,
        size: Int = 20
    ): AirlineResponse
    
    /**
     * Get airline by code
     */
    fun getAirlineByCode(code: String): Airline
    
    /**
     * Create a new airline
     */
    fun createAirline(airline: Airline): Airline
    
    /**
     * Update an existing airline
     */
    fun updateAirline(code: String, airline: Airline): Airline
    
    /**
     * Delete an airline
     */
    fun deleteAirline(code: String)
    
    /**
     * Get airlines by alliance code
     */
    fun getAirlinesByAlliance(allianceCode: String): List<Airline>
}
