package com.windsurf.booking.product.repository.elasticsearch

import com.windsurf.booking.product.domain.model.Stop
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import java.time.Instant

/**
 * Elasticsearch repository for Stop entity
 */
@Repository
interface StopElasticsearchRepository : ElasticsearchRepository<Stop, String> {
    fun findByFlightId(flightId: String): List<Stop>
    fun findByArrivalAirportCodeOrDepartureAirportCode(arrivalCode: String, departureCode: String): List<Stop>
    fun findByAircraftId(aircraftId: String): List<Stop>
    fun findByDepartureTimeBetween(start: Instant, end: Instant): List<Stop>
}
