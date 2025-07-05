package com.windsurf.booking.product.domain.model

import java.time.Instant

/**
 * Domain model for Airport
 */
data class Airport(
    val code: String,
    val name: String,
    val city: String,
    val country: String,
    val timezone: String,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)
