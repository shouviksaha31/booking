package com.windsurf.booking.product.domain.model

import com.windsurf.booking.common.domain.BaseEntity
import java.time.Instant

/**
 * Represents an airline in the system
 */
data class Airline(
    val airlineCode: String,
    val name: String,
    val logoUrl: String?,
    val country: String,
    val active: Boolean,
    val allianceCode: String?,
    val allianceName: String?,
    override val createdAt: Instant,
    override val updatedAt: Instant
) : BaseEntity()
