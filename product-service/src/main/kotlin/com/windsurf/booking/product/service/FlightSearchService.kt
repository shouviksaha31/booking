package com.windsurf.booking.product.service

import com.windsurf.booking.product.api.model.FlightSearchResponse
import java.time.LocalDate

/**
 * Service for searching flights based on various criteria
 */
interface FlightSearchService {
    /**
     * Search for flights based on origin, destination, and dates
     */
    fun searchFlights(
        origin: String,
        destination: String,
        departureDate: LocalDate,
        returnDate: LocalDate? = null,
        passengers: Int = 1,
        cabinClass: String = "ECONOMY",
        page: Int = 0,
        size: Int = 20
    ): FlightSearchResponse
    
    /**
     * Search for flights with flexible dates (±3 days)
     */
    fun searchFlightsWithFlexibleDates(
        origin: String,
        destination: String,
        departureDate: LocalDate,
        returnDate: LocalDate? = null,
        passengers: Int = 1,
        cabinClass: String = "ECONOMY"
    ): Map<LocalDate, FlightSearchResponse>
}
