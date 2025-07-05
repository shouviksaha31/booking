package com.windsurf.booking.product.domain.model

import com.windsurf.booking.common.domain.BaseEntity
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * Represents a flight in the system
 */
data class Flight(
    val flightId: String,
    val flightNumber: String,
    val airlineCode: String,
    val airlineName: String,
    val date: LocalDate,
    val flightStartTime: Instant,
    val flightEndTime: Instant,
    val source: String,
    val destination: String,
    val stops: List<Stop>,
    val priceMap: Map<PassengerType, BigDecimal>,
    val status: FlightStatus,
    override val createdAt: Instant,
    override val updatedAt: Instant
) : BaseEntity()

/**
 * Enum representing the status of a flight
 */
enum class FlightStatus {
    SCHEDULED,
    BOARDING,
    DEPARTED,
    IN_AIR,
    LANDED,
    DELAYED,
    CANCELLED
}

/**
 * Enum representing different passenger types for pricing
 */
enum class PassengerType {
    ADULT,
    CHILD,
    INFANT,
    SENIOR,
    STUDENT,
    MILITARY
}
