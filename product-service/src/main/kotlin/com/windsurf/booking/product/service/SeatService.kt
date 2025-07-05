package com.windsurf.booking.product.service

import com.windsurf.booking.product.api.model.Seat
import com.windsurf.booking.product.api.model.SeatResponse

/**
 * Service for managing seat information
 */
interface SeatService {
    /**
     * Get all seats for a flight
     */
    fun getSeatsForFlight(flightId: String): SeatResponse
    
    /**
     * Get a specific seat by ID
     */
    fun getSeatById(seatId: String): Seat
    
    /**
     * Update seat availability
     */
    fun updateSeatAvailability(seatId: String, available: Boolean): Seat
    
    /**
     * Get available seats for a flight by cabin class
     */
    fun getAvailableSeatsByClass(flightId: String, cabinClass: String): List<Seat>
}
