package com.windsurf.booking.product.repository.sql

import com.windsurf.booking.product.domain.model.Airport

/**
 * Repository interface for Airport entity
 */
interface AirportRepository {
    fun findByCode(airportCode: String): Airport?
    fun findAll(): List<Airport>
    fun findByCountry(country: String): List<Airport>
    fun findByCity(city: String): List<Airport>
    fun save(airport: Airport): Airport
    fun update(airport: Airport): Airport
    fun delete(airportCode: String): Boolean
}
