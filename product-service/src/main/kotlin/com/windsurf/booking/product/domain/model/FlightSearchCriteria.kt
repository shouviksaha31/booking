package com.windsurf.booking.product.domain.model

import java.time.LocalDate

/**
 * Search criteria for finding flights
 */
data class FlightSearchCriteria(
    val origin: String,
    val destination: String,
    val departureDate: LocalDate,
    val returnDate: LocalDate? = null,
    val passengers: Map<PassengerType, Int>,
    val preferredAirlines: List<String> = emptyList(),
    val maxStops: Int? = null,
    val preferredCabinClass: SeatType? = null,
    val flexibleDates: Boolean = false,
    val directFlightsOnly: Boolean = false,
    val page: Int = 0,
    val size: Int = 20
)
