package com.windsurf.booking.product.repository.sql

import com.windsurf.booking.product.domain.model.Airline
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class AirlineRepositoryImpl(private val dsl: DSLContext) : AirlineRepository {

    private val airlines = DSL.table("airlines")
    private val airlineCode = DSL.field("airline_code")
    private val name = DSL.field("name")
    private val logoUrl = DSL.field("logo_url")
    private val country = DSL.field("country")
    private val active = DSL.field("active")
    private val allianceCode = DSL.field("alliance_code")
    private val allianceName = DSL.field("alliance_name")
    private val createdAt = DSL.field("created_at")
    private val updatedAt = DSL.field("updated_at")

    override fun findByCode(airlineCode: String): Airline? {
        return dsl.select()
            .from(airlines)
            .where(this.airlineCode.eq(airlineCode))
            .fetchOne()
            ?.toAirline()
    }

    override fun findAll(): List<Airline> {
        return dsl.select()
            .from(airlines)
            .fetch()
            .map { it.toAirline() }
    }

    override fun findAllActive(): List<Airline> {
        return dsl.select()
            .from(airlines)
            .where(active.eq(true))
            .fetch()
            .map { it.toAirline() }
    }

    override fun save(airline: Airline): Airline {
        val now = Instant.now()
        
        dsl.insertInto(airlines)
            .set(airlineCode, airline.airlineCode)
            .set(name, airline.name)
            .set(logoUrl, airline.logoUrl)
            .set(country, airline.country)
            .set(active, airline.active)
            .set(allianceCode, airline.allianceCode)
            .set(allianceName, airline.allianceName)
            .set(createdAt, now)
            .set(updatedAt, now)
            .execute()
            
        return airline.copy(createdAt = now, updatedAt = now)
    }

    override fun update(airline: Airline): Airline {
        val now = Instant.now()
        
        dsl.update(airlines)
            .set(name, airline.name)
            .set(logoUrl, airline.logoUrl)
            .set(country, airline.country)
            .set(active, airline.active)
            .set(allianceCode, airline.allianceCode)
            .set(allianceName, airline.allianceName)
            .set(updatedAt, now)
            .where(airlineCode.eq(airline.airlineCode))
            .execute()
            
        return airline.copy(updatedAt = now)
    }

    override fun delete(airlineCode: String): Boolean {
        return dsl.deleteFrom(airlines)
            .where(this.airlineCode.eq(airlineCode))
            .execute() > 0
    }

    override fun findByAlliance(allianceCode: String): List<Airline> {
        return dsl.select()
            .from(airlines)
            .where(this.allianceCode.eq(allianceCode))
            .fetch()
            .map { it.toAirline() }
    }
    
    private fun Record.toAirline(): Airline {
        return Airline(
            airlineCode = this.get(airlineCode, String::class.java),
            name = this.get(name, String::class.java),
            logoUrl = this.get(logoUrl, String::class.java),
            country = this.get(country, String::class.java),
            active = this.get(active, Boolean::class.java),
            allianceCode = this.get(allianceCode, String::class.java),
            allianceName = this.get(allianceName, String::class.java),
            createdAt = this.get(createdAt, Instant::class.java),
            updatedAt = this.get(updatedAt, Instant::class.java)
        )
    }
}
