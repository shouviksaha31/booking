package com.windsurf.booking.product.service.impl

import com.windsurf.booking.product.api.model.Airport
import com.windsurf.booking.product.api.model.AirportResponse
import com.windsurf.booking.product.repository.sql.AirportRepository
import com.windsurf.booking.product.service.AirportService
import com.windsurf.booking.product.service.mapAirportEntityToApiModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.NoSuchElementException

@Service
class AirportServiceImpl(
    private val airportRepository: AirportRepository
) : AirportService {

    private val logger = LoggerFactory.getLogger(AirportServiceImpl::class.java)

    override fun getAirports(
        query: String?,
        country: String?,
        page: Int,
        size: Int
    ): AirportResponse {
        logger.info("Getting airports with query: $query, country: $country, page: $page, size: $size")
        
        // Get airports based on filters
        val allAirports = when {
            country != null -> airportRepository.findByCountry(country)
            else -> airportRepository.findAll()
        }
        
        // Apply additional filtering if query is provided
        val filteredAirports = if (query != null) {
            allAirports.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.code.contains(query, ignoreCase = true) 
            }
        } else {
            allAirports
        }
        
        // Apply pagination
        val offset = page * size
        val paginatedAirports = filteredAirports
            .drop(offset)
            .take(size)
        
        // Get total count
        val totalCount = filteredAirports.size
        
        // Convert to API models
        val airports = paginatedAirports.map { airportEntity ->
            mapAirportEntityToApiModel(airportEntity)
        }
        
        return AirportResponse(
            airports = airports,
            totalResults = totalCount,
            pageNumber = page,
            pageSize = size
        )
    }
    
    override fun getAirportByCode(code: String): Airport {
        logger.info("Getting airport with code: $code")
        
        val airportEntity = airportRepository.findByCode(code)
            ?: throw NoSuchElementException("Airport not found with code: $code")
        
        return mapAirportEntityToApiModel(airportEntity)
    }
    
    override fun createAirport(airport: Airport): Airport {
        logger.info("Creating new airport: ${airport.code}")
        
        // Check if airport with this code already exists
        if (airportRepository.findByCode(airport.code!!) != null) {
            throw IllegalArgumentException("Airport with code ${airport.code} already exists")
        }
        
        // Convert API model to entity
        val airportEntity = com.windsurf.booking.product.domain.model.Airport(
            code = airport.code!!,
            name = airport.name!!,
            city = airport.city ?: "",
            country = airport.country ?: "",
            timezone = airport.timezone ?: "UTC",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        
        // Save entity
        val savedEntity = airportRepository.save(airportEntity)
        
        return mapAirportEntityToApiModel(savedEntity)
    }
    
    override fun updateAirport(code: String, airport: Airport): Airport {
        logger.info("Updating airport with code: $code")
        
        // Check if airport exists
        val existingAirport = airportRepository.findByCode(code)
            ?: throw NoSuchElementException("Airport not found with code: $code")
        
        // Update entity fields
        val updatedEntity = existingAirport.copy(
            name = airport.name ?: existingAirport.name,
            city = airport.city ?: existingAirport.city,
            country = airport.country ?: existingAirport.country,
            timezone = airport.timezone ?: existingAirport.timezone,
            updatedAt = Instant.now()
        )
        
        // Save updated entity
        val savedEntity = airportRepository.update(updatedEntity)
        
        return mapAirportEntityToApiModel(savedEntity)
    }
    
    override fun deleteAirport(code: String) {
        logger.info("Deleting airport with code: $code")
        
        // Check if airport exists
        if (airportRepository.findByCode(code) == null) {
            throw NoSuchElementException("Airport not found with code: $code")
        }
        
        // Delete airport
        airportRepository.delete(code)
    }
}
