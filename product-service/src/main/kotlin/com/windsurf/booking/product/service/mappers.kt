package com.windsurf.booking.product.service

import com.windsurf.booking.product.api.model.Aircraft
import com.windsurf.booking.product.api.model.Airline
import com.windsurf.booking.product.api.model.Airport
import com.windsurf.booking.product.api.model.Flight
import com.windsurf.booking.product.api.model.Seat
import com.windsurf.booking.product.api.model.SeatMap
import com.windsurf.booking.product.domain.model.Airline as AirlineDomain
import com.windsurf.booking.product.domain.model.Airport as AirportDomain
import com.windsurf.booking.product.domain.model.Flight as FlightDomain
import com.windsurf.booking.product.domain.model.Seat as SeatDomain
import com.windsurf.booking.product.domain.model.Stop as StopDomain
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * Maps an airport domain entity to an API model
 */
fun mapAirportEntityToApiModel(airportEntity: AirportDomain): Airport {
    return Airport(
        code = airportEntity.code,
        name = airportEntity.name,
        city = airportEntity.city,
        country = airportEntity.country,
        timezone = airportEntity.timezone
    )
}

/**
 * Maps an airline domain entity to an API model
 */
fun mapAirlineEntityToApiModel(airlineEntity: AirlineDomain): Airline {
    val apiModel = Airline(
    airlineCode = airlineEntity.airlineCode,
    name = airlineEntity.name,
    logoUrl = airlineEntity.logoUrl,
    country = airlineEntity.country,
    active = airlineEntity.active,
    allianceCode = airlineEntity.allianceCode,
    allianceName = airlineEntity.allianceName
    )
    return apiModel
}

/**
 * Maps a flight domain entity to an API model
 */
fun mapFlightEntityToApiModel(flightEntity: FlightDomain): Flight {
    return Flight(
        id = flightEntity.flightId,
        flightNumber = flightEntity.flightNumber,
        airline = Airline(name = flightEntity.airlineName),
        origin = Airport(name = flightEntity.source),
        destination = Airport(name = flightEntity.destination),
        departureTime = OffsetDateTime.ofInstant(
            flightEntity.flightStartTime,
            ZoneOffset.UTC
        ),
        arrivalTime = OffsetDateTime.ofInstant(
            flightEntity.flightEndTime,
            ZoneOffset.UTC
        ),
        duration = calculateDurationMinutes(flightEntity.flightStartTime, flightEntity.flightEndTime),
        stops = flightEntity.stops.map { mapStopEntityToApiModel(it) },
        aircraft = null, // Not available in domain model
        status = Flight.Status.valueOf(flightEntity.status.name),
        priceMap = flightEntity.priceMap.mapKeys { it.key.name }
            .mapValues { it.value.toDouble() }
    )
}

/**
 * Maps a flight domain entity with stops to an API model
 */
fun mapFlightEntityToApiModel(flightEntity: FlightDomain, stopEntities: List<StopDomain>): Flight {
    // Calculate duration in minutes
    val durationMinutes = ChronoUnit.MINUTES.between(
        flightEntity.flightStartTime,
        flightEntity.flightEndTime
    )
    
    // Map stops
    val stops = stopEntities.map { stopEntity ->
        mapStopEntityToApiModel(stopEntity)
    }
    
    // Create aircraft model using constructor
    val aircraft = Aircraft(
        aircraftId = stopEntities.firstOrNull()?.aircraftId ?: "",
        model = stopEntities.firstOrNull()?.aircraftType ?: "",
        manufacturer = "Unknown", // Default value as it's not in the domain model
        capacity = 0, // Default value as it's not in the domain model
        type = stopEntities.firstOrNull()?.aircraftType ?: ""
    )
    
    // Create and return flight model using constructor
    return Flight(
        id = flightEntity.flightId,
        flightNumber = flightEntity.flightNumber,
        airline = Airline(
            name = flightEntity.airlineName,
            airlineCode = flightEntity.airlineCode
        ),
        origin = Airport(name = flightEntity.source),
        destination = Airport(name = flightEntity.destination),
        departureTime = OffsetDateTime.ofInstant(flightEntity.flightStartTime, ZoneOffset.UTC),
        arrivalTime = OffsetDateTime.ofInstant(flightEntity.flightEndTime, ZoneOffset.UTC),
        duration = durationMinutes.toInt(),
        stops = stops,
        aircraft = aircraft,
        status = Flight.Status.valueOf(flightEntity.status.name),
        priceMap = flightEntity.priceMap.mapKeys { it.key.name }.mapValues { it.value.toDouble() }
    )
}

/**
 * Maps a seat domain entity to an API model
 */
fun mapSeatEntityToApiModel(seatEntity: SeatDomain): Seat {
    return Seat(
        seatId = seatEntity.seatId,
        stopId = seatEntity.stopId,
        seatNumber = seatEntity.seatNumber,
        type = Seat.Type.valueOf(seatEntity.type.name),
        price = seatEntity.price.toDouble(),
        available = seatEntity.available,
        features = seatEntity.features.map { Seat.Features.valueOf(it.name) }
    )
}

/**
 * Maps a stop domain entity to an API model
 */
fun mapStopEntityToApiModel(stopEntity: StopDomain): com.windsurf.booking.product.api.model.Stop {
    return com.windsurf.booking.product.api.model.Stop(
        stopId = stopEntity.stopId,
        flightId = stopEntity.flightId,
        departureAirport = mapAirportEntityToApiModel(stopEntity.departureAirport),
        arrivalAirport = mapAirportEntityToApiModel(stopEntity.arrivalAirport),
        departureTime = OffsetDateTime.ofInstant(
            stopEntity.departureTime,
            ZoneOffset.UTC
        ),
        arrivalTime = OffsetDateTime.ofInstant(
            stopEntity.arrivalTime,
            ZoneOffset.UTC
        ),
        aircraft = Aircraft(
            aircraftId = stopEntity.aircraftId,
            model = stopEntity.aircraftType,
            manufacturer = "Unknown", // Default value as it's not in the domain model
            capacity = 0, // Default value as it's not in the domain model
            type = stopEntity.aircraftType
        ),
        availableSeats = stopEntity.seats.count { it.available }
    )
}

/**
 * Generates a seat map from a list of seats
 */
fun generateSeatMap(seats: List<Seat>): SeatMap {
    // In a real implementation, we would generate a proper seat map
    // based on the aircraft configuration and seat positions

    // For now, we'll create a simple placeholder seat map
    val rows = 30
    val columns = 6

    // Create a 2D array of seat numbers
    val layout = Array(rows) { row ->
        Array(columns) { col ->
            val seatNumber = "${(row + 1)}${('A'.code + col).toChar()}"
            seatNumber
        }
    }

    // Create and return seat map using constructor
    return SeatMap(
        rows = rows,
        columns = columns,
        layout = layout.map { it.toList() }
    )
}

/**
 * Calculate duration in minutes between two instants
 */
fun calculateDurationMinutes(start: Instant, end: Instant): Int {
    return ((end.toEpochMilli() - start.toEpochMilli()) / (1000 * 60)).toInt()
}
