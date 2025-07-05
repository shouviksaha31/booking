package com.windsurf.booking.product.service

import com.windsurf.booking.product.api.model.Airport
import com.windsurf.booking.product.api.model.AirportResponse

/**
 * Service for managing airport information
 */
interface AirportService {
    /**
     * Get airports with optional filtering
     */
    fun getAirports(
        query: String? = null,
        country: String? = null,
        page: Int = 0,
        size: Int = 20
    ): AirportResponse
    
    /**
     * Get airport by code
     */
    fun getAirportByCode(code: String): Airport
    
    /**
     * Create a new airport
     */
    fun createAirport(airport: Airport): Airport
    
    /**
     * Update an existing airport
     */
    fun updateAirport(code: String, airport: Airport): Airport
    
    /**
     * Delete an airport
     */
    fun deleteAirport(code: String)
}
