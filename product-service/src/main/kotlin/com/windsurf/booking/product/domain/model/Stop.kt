package com.windsurf.booking.product.domain.model

import com.windsurf.booking.common.domain.BaseEntity
import java.time.Instant

/**
 * Represents a stop in a flight journey
 */
data class Stop(
    val stopId: String,
    val flightId: String,
    val arrivalTime: Instant,
    val departureTime: Instant,
    val arrivalAirport: Airport,
    val departureAirport: Airport,
    val seats: List<Seat>,
    val aircraftId: String,
    val aircraftType: String,
    val stopSequence: Int,
    val terminal: String?,
    val gate: String?,
    override val createdAt: Instant,
    override val updatedAt: Instant
) : BaseEntity()

/**
 * Represents an airport
 */
data class Airport(
    val code: String,
    val name: String,
    val city: String,
    val country: String,
    val timezone: String
)
