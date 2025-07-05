package com.windsurf.booking.product.domain.model

import com.windsurf.booking.common.domain.BaseEntity
import java.math.BigDecimal
import java.time.Instant

/**
 * Represents a seat on a flight
 */
data class Seat(
    val seatId: String,
    val stopId: String,
    val seatNumber: String,
    val type: SeatType,
    val price: BigDecimal,
    val available: Boolean,
    val features: List<SeatFeature>,
    val row: Int,
    val column: String,
    override val createdAt: Instant,
    override val updatedAt: Instant
) : BaseEntity()

/**
 * Enum representing the type of seat
 */
enum class SeatType {
    ECONOMY,
    PREMIUM_ECONOMY,
    BUSINESS,
    FIRST
}

/**
 * Enum representing special features of a seat
 */
enum class SeatFeature {
    EXTRA_LEGROOM,
    WINDOW,
    AISLE,
    MIDDLE,
    EXIT_ROW,
    BULKHEAD,
    BASSINET,
    POWER_OUTLET,
    USB_PORT,
    RECLINABLE
}
