package com.windsurf.booking.product.service.impl

import com.windsurf.booking.product.api.model.Airline
import com.windsurf.booking.product.api.model.AirlineResponse
import com.windsurf.booking.product.domain.model.Airline as AirlineDomain
import com.windsurf.booking.product.repository.sql.AirlineRepository
import com.windsurf.booking.product.service.AirlineService
import com.windsurf.booking.product.service.mapAirlineEntityToApiModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.NoSuchElementException

@Service
class AirlineServiceImpl(
    private val airlineRepository: AirlineRepository
) : AirlineService {

    private val logger = LoggerFactory.getLogger(AirlineServiceImpl::class.java)

    override fun getAirlines(
        query: String?,
        page: Int,
        size: Int
    ): AirlineResponse {
        logger.info("Getting airlines with query: $query, page: $page, size: $size")
        
        // Get airlines based on filters
        val allAirlines = airlineRepository.findAll()
        
        // Apply additional filtering if query is provided
        val filteredAirlines = if (query != null) {
            allAirlines.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.airlineCode.contains(query, ignoreCase = true) 
            }
        } else {
            allAirlines
        }
        
        // Apply pagination
        val offset = page * size
        val paginatedAirlines = filteredAirlines
            .drop(offset)
            .take(size)
        
        // Get total count
        val totalCount = filteredAirlines.size
        
        // Convert to API models
        val airlines = paginatedAirlines.map { airlineEntity ->
            mapAirlineEntityToApiModel(airlineEntity)
        }
        
        return AirlineResponse(
            airlines = airlines,
            totalResults = totalCount,
            pageNumber = page,
            pageSize = size
        )
    }
    
    override fun getAirlineByCode(code: String): Airline {
        logger.info("Getting airline with code: $code")
        
        val airlineEntity = airlineRepository.findByCode(code)
            ?: throw NoSuchElementException("Airline not found with code: $code")
        
        return mapAirlineEntityToApiModel(airlineEntity)
    }
    
    override fun createAirline(airline: Airline): Airline {
        logger.info("Creating new airline: ${airline.airlineCode}")
        
        // Check if airline with this code already exists
        if (airlineRepository.findByCode(airline.airlineCode!!) != null) {
            throw IllegalArgumentException("Airline with code ${airline.airlineCode} already exists")
        }
        
        // Convert API model to entity
        val airlineEntity = AirlineDomain(
            airlineCode = airline.airlineCode!!,
            name = airline.name ?: "",
            logoUrl = airline.logoUrl,
            country = airline.country ?: "",
            active = airline.active ?: false,
            allianceCode = airline.allianceCode,
            allianceName = airline.allianceName,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        
        // Save entity
        val savedEntity = airlineRepository.save(airlineEntity)
        
        return mapAirlineEntityToApiModel(savedEntity)
    }
    
    override fun updateAirline(code: String, airline: Airline): Airline {
        logger.info("Updating airline with code: $code")
        
        // Check if airline exists
        val existingAirline = airlineRepository.findByCode(code)
            ?: throw NoSuchElementException("Airline not found with code: $code")
        
        // Update entity fields
        val updatedEntity = existingAirline.copy(
            name = airline.name ?: existingAirline.name,
            logoUrl = airline.logoUrl ?: existingAirline.logoUrl,
            country = airline.country ?: existingAirline.country,
            active = airline.active ?: existingAirline.active,
            allianceCode = airline.allianceCode ?: existingAirline.allianceCode,
            allianceName = airline.allianceName ?: existingAirline.allianceName,
            updatedAt = Instant.now()
        )
        
        // Save updated entity
        val savedEntity = airlineRepository.update(updatedEntity)
        
        return mapAirlineEntityToApiModel(savedEntity)
    }
    
    override fun deleteAirline(code: String) {
        logger.info("Deleting airline with code: $code")
        
        // Check if airline exists
        if (airlineRepository.findByCode(code) == null) {
            throw NoSuchElementException("Airline not found with code: $code")
        }
        
        // Delete airline
        airlineRepository.delete(code)
    }
    
    override fun getAirlinesByAlliance(allianceCode: String): List<Airline> {
        logger.info("Getting airlines by alliance code: $allianceCode")
        
        val airlineEntities = airlineRepository.findByAlliance(allianceCode)
        
        return airlineEntities.map { airlineEntity ->
            mapAirlineEntityToApiModel(airlineEntity)
        }
    }
}
