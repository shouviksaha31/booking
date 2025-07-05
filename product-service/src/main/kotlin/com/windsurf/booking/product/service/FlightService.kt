package com.windsurf.booking.product.service

import com.windsurf.booking.product.api.model.Flight

/**
 * Service for managing flight information
 */
interface FlightService {
    /**
     * Get flight by ID
     */
    fun getFlightById(flightId: String): Flight
    
    /**
     * Create a new flight
     */
    fun createFlight(flight: Flight): Flight
    
    /**
     * Update an existing flight
     */
    fun updateFlight(flightId: String, flight: Flight): Flight
    
    /**
     * Delete a flight
     */
    fun deleteFlight(flightId: String)
}
