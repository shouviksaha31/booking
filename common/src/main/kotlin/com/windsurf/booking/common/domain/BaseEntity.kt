package com.windsurf.booking.common.domain

import java.time.Instant

/**
 * Base class for all domain entities with common fields
 */
abstract class BaseEntity {
    /**
     * The timestamp when this entity was created
     */
    abstract val createdAt: Instant
    
    /**
     * The timestamp when this entity was last updated
     */
    abstract val updatedAt: Instant
}
