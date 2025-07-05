package com.windsurf.booking.product.repository.elasticsearch

import com.windsurf.booking.product.domain.model.Flight
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * Elasticsearch repository for Flight entity
 */
@Repository
interface FlightElasticsearchRepository : ElasticsearchRepository<Flight, String> {
    fun findBySourceAndDestinationAndDate(source: String, destination: String, date: LocalDate): List<Flight>
    fun findByAirlineCode(airlineCode: String): List<Flight>
    fun findByFlightNumber(flightNumber: String): List<Flight>
    fun findBySourceAndDestination(source: String, destination: String): List<Flight>
    fun findByDate(date: LocalDate): List<Flight>
}
