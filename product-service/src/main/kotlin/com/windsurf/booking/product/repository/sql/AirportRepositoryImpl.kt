package com.windsurf.booking.product.repository.sql

import com.windsurf.booking.product.domain.model.Airport
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class AirportRepositoryImpl(private val dsl: DSLContext) : AirportRepository {

    private val airports = DSL.table("airports")
    private val code = DSL.field("code")
    private val name = DSL.field("name")
    private val city = DSL.field("city")
    private val country = DSL.field("country")
    private val latitude = DSL.field("latitude")
    private val longitude = DSL.field("longitude")
    private val timezone = DSL.field("timezone")
    private val createdAt = DSL.field("created_at")
    private val updatedAt = DSL.field("updated_at")

    override fun findByCode(airportCode: String): Airport? {
        return dsl.select()
            .from(airports)
            .where(code.eq(airportCode))
            .fetchOne()
            ?.toAirport()
    }

    override fun findAll(): List<Airport> {
        return dsl.select()
            .from(airports)
            .fetch()
            .map { it.toAirport() }
    }

    override fun findByCountry(country: String): List<Airport> {
        return dsl.select()
            .from(airports)
            .where(this.country.eq(country))
            .fetch()
            .map { it.toAirport() }
    }

    override fun findByCity(city: String): List<Airport> {
        return dsl.select()
            .from(airports)
            .where(this.city.eq(city))
            .fetch()
            .map { it.toAirport() }
    }

    override fun save(airport: Airport): Airport {
        val now = Instant.now()
        
        dsl.insertInto(airports)
            .set(code, airport.code)
            .set(name, airport.name)
            .set(city, airport.city)
            .set(country, airport.country)
            .set(timezone, airport.timezone)
            .set(createdAt, now)
            .set(updatedAt, now)
            .execute()
            
        return airport.copy(createdAt = now, updatedAt = now)
    }

    override fun update(airport: Airport): Airport {
        val now = Instant.now()
        
        dsl.update(airports)
            .set(name, airport.name)
            .set(city, airport.city)
            .set(country, airport.country)
            .set(timezone, airport.timezone)
            .set(updatedAt, now)
            .where(code.eq(airport.code))
            .execute()
            
        return airport.copy(updatedAt = now)
    }

    override fun delete(airportCode: String): Boolean {
        return dsl.deleteFrom(airports)
            .where(code.eq(airportCode))
            .execute() > 0
    }
    
    // Additional helper methods for AirportServiceImpl
    
    fun findByNameOrCode(query: String, offset: Int, size: Int): List<Airport> {
        return dsl.select()
            .from(airports)
            .where(name.like("%$query%").or(code.like("%$query%")))
            .limit(size)
            .offset(offset)
            .fetch()
            .map { it.toAirport() }
    }
    
    fun findByNameOrCodeAndCountry(query: String, country: String, offset: Int, size: Int): List<Airport> {
        return dsl.select()
            .from(airports)
            .where(
                (name.like("%$query%").or(code.like("%$query%")))
                .and(this.country.eq(country))
            )
            .limit(size)
            .offset(offset)
            .fetch()
            .map { it.toAirport() }
    }
    
    fun findAll(offset: Int, size: Int): List<Airport> {
        return dsl.select()
            .from(airports)
            .limit(size)
            .offset(offset)
            .fetch()
            .map { it.toAirport() }
    }
    
    fun findByCountry(country: String, offset: Int, size: Int): List<Airport> {
        return dsl.select()
            .from(airports)
            .where(this.country.eq(country))
            .limit(size)
            .offset(offset)
            .fetch()
            .map { it.toAirport() }
    }
    
    fun countByNameOrCode(query: String): Int {
        return dsl.selectCount()
            .from(airports)
            .where(name.like("%$query%").or(code.like("%$query%")))
            .fetchOne(0, Int::class.java) ?: 0
    }
    
    fun countByNameOrCodeAndCountry(query: String, country: String): Int {
        return dsl.selectCount()
            .from(airports)
            .where(
                (name.like("%$query%").or(code.like("%$query%")))
                .and(this.country.eq(country))
            )
            .fetchOne(0, Int::class.java) ?: 0
    }
    
    fun countByCountry(country: String): Int {
        return dsl.selectCount()
            .from(airports)
            .where(this.country.eq(country))
            .fetchOne(0, Int::class.java) ?: 0
    }
    
    fun count(): Int {
        return dsl.selectCount()
            .from(airports)
            .fetchOne(0, Int::class.java) ?: 0
    }
    
    private fun Record.toAirport(): Airport {
        return Airport(
            code = this.get(code, String::class.java),
            name = this.get(name, String::class.java),
            city = this.get(city, String::class.java),
            country = this.get(country, String::class.java),
            timezone = this.get(timezone, String::class.java),
            createdAt = this.get(createdAt, Instant::class.java),
            updatedAt = this.get(updatedAt, Instant::class.java)
        )
    }
}
