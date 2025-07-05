package com.windsurf.booking.product.repository.elasticsearch

import com.windsurf.booking.product.domain.model.Flight
import com.windsurf.booking.product.domain.model.FlightSearchCriteria
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
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
        val boolQuery = QueryBuilders.boolQuery()
        
        // Add required criteria
        boolQuery.must(QueryBuilders.termQuery("source", criteria.origin))
        boolQuery.must(QueryBuilders.termQuery("destination", criteria.destination))
        boolQuery.must(QueryBuilders.termQuery("date", criteria.departureDate.toString()))
        
        // Add optional filters
        addOptionalFilters(boolQuery, criteria)
        
        val searchQuery = NativeSearchQueryBuilder()
            .withQuery(boolQuery)
            .withSort(SortBuilders.fieldSort("flightStartTime").order(SortOrder.ASC))
            .withPageable(PageRequest.of(criteria.page, criteria.size))
            .build()
        
        return elasticsearchOperations.search(searchQuery, Flight::class.java)
            .map(SearchHit<Flight>::getContent)
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
    
    private fun addOptionalFilters(boolQuery: BoolQueryBuilder, criteria: FlightSearchCriteria) {
        // Filter by preferred airlines if specified
        if (criteria.preferredAirlines.isNotEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery("airlineCode", criteria.preferredAirlines))
        }
        
        // Filter by direct flights if requested
        if (criteria.directFlightsOnly) {
            boolQuery.filter(QueryBuilders.termQuery("stops.size", 0))
        }
        
        // Filter by maximum number of stops if specified
        if (criteria.maxStops != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("stops.size").lte(criteria.maxStops))
        }
    }
}
