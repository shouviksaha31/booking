package com.windsurf.booking.product.repository.elasticsearch

import com.windsurf.booking.product.domain.model.Flight
import com.windsurf.booking.product.domain.model.FlightSearchCriteria
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * Custom repository for advanced flight searches
 */
@Repository
class FlightSearchRepository(private val elasticsearchOperations: ElasticsearchOperations) {

    /**
     * Search for flights based on search criteria
     */
    fun searchFlights(criteria: FlightSearchCriteria): List<Flight> {
        // Create criteria query
        val searchCriteria = Criteria("source").`is`(criteria.origin)
            .and(Criteria("destination").`is`(criteria.destination))
            .and(Criteria("date").`is`(criteria.departureDate.toString()))
        
        // Add optional filters
        addOptionalFilters(searchCriteria, criteria)
        
        val query: Query = CriteriaQuery(searchCriteria)
            .setPageable(PageRequest.of(criteria.page, criteria.size))
        
        return elasticsearchOperations.search(query, Flight::class.java)
            .map(SearchHit<Flight>::getContent)
            .filter { if (criteria.maxStops != null) it.stops.size <= criteria.maxStops else true }
            .toList()
    }
    
    /**
     * Search for return flights based on search criteria
     */
    fun searchReturnFlights(criteria: FlightSearchCriteria): List<Flight> {
        if (criteria.returnDate == null) {
            return emptyList()
        }
        
        // Create a new search criteria with origin and destination swapped
        val returnCriteria = criteria.copy(
            origin = criteria.destination,
            destination = criteria.origin,
            departureDate = criteria.returnDate
        )
        
        return searchFlights(returnCriteria)
    }
    
    /**
     * Search for flights with flexible dates (±3 days)
     */
    fun searchFlexibleDates(criteria: FlightSearchCriteria): Map<LocalDate, List<Flight>> {
        if (!criteria.flexibleDates) {
            return mapOf(criteria.departureDate to searchFlights(criteria))
        }
        
        val result = mutableMapOf<LocalDate, List<Flight>>()
        
        // Search for flights on the specified date and 3 days before and after
        for (dayOffset in -3..3) {
            val date = criteria.departureDate.plusDays(dayOffset.toLong())
            val dateCriteria = criteria.copy(departureDate = date)
            result[date] = searchFlights(dateCriteria)
        }
        
        return result
    }
    
    private fun addOptionalFilters(criteria: Criteria, searchCriteria: FlightSearchCriteria) {
        // Filter by preferred airlines if specified
        if (searchCriteria.preferredAirlines.isNotEmpty()) {
            criteria.and(Criteria("airlineCode").`in`(searchCriteria.preferredAirlines))
        }
        
        // Filter by direct flights if requested
        if (searchCriteria.directFlightsOnly) {
            criteria.and(Criteria("stops").empty())
        }
    }
}
