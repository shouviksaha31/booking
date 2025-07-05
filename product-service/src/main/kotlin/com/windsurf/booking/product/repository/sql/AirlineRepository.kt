package com.windsurf.booking.product.repository.sql

import com.windsurf.booking.product.domain.model.Airline
import java.time.Instant

/**
 * Repository interface for Airline entity
 */
interface AirlineRepository {
    fun findByCode(airlineCode: String): Airline?
    fun findAll(): List<Airline>
    fun findAllActive(): List<Airline>
    fun save(airline: Airline): Airline
    fun update(airline: Airline): Airline
    fun delete(airlineCode: String): Boolean
    fun findByAlliance(allianceCode: String): List<Airline>
}
