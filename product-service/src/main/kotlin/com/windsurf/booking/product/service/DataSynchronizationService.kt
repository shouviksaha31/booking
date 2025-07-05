package com.windsurf.booking.product.service

import com.windsurf.booking.product.domain.model.Flight
import com.windsurf.booking.product.domain.model.Seat
import com.windsurf.booking.product.domain.model.Stop
import com.windsurf.booking.product.repository.elasticsearch.FlightElasticsearchRepository
import com.windsurf.booking.product.repository.elasticsearch.SeatElasticsearchRepository
import com.windsurf.booking.product.repository.elasticsearch.StopElasticsearchRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service responsible for synchronizing data between SQL database and Elasticsearch
 * Uses event-driven approach with Kafka for data consistency
 */
@Service
class DataSynchronizationService(
    private val flightRepository: FlightElasticsearchRepository,
    private val stopRepository: StopElasticsearchRepository,
    private val seatRepository: SeatElasticsearchRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(DataSynchronizationService::class.java)
    
    companion object {
        const val FLIGHT_UPDATED_TOPIC = "flight-events"
    }

    /**
     * Synchronize flight data to Elasticsearch
     */
    @Transactional
    fun syncFlight(flight: Flight) {
        logger.info("Synchronizing flight with ID: ${flight.flightId}")
        flightRepository.save(flight)
        
        // Publish event for other services
        kafkaTemplate.send(FLIGHT_UPDATED_TOPIC, flight.flightId, flight)
    }

    /**
     * Synchronize stop data to Elasticsearch
     */
    @Transactional
    fun syncStop(stop: Stop) {
        logger.info("Synchronizing stop with ID: ${stop.stopId}")
        stopRepository.save(stop)
    }

    /**
     * Synchronize seat data to Elasticsearch
     */
    @Transactional
    fun syncSeat(seat: Seat) {
        logger.info("Synchronizing seat with ID: ${seat.seatId}")
        seatRepository.save(seat)
    }

    /**
     * Bulk synchronize multiple flights
     */
    @Transactional
    fun syncFlights(flights: List<Flight>) {
        logger.info("Bulk synchronizing ${flights.size} flights")
        flightRepository.saveAll(flights)
    }

    /**
     * Bulk synchronize multiple stops
     */
    @Transactional
    fun syncStops(stops: List<Stop>) {
        logger.info("Bulk synchronizing ${stops.size} stops")
        stopRepository.saveAll(stops)
    }

    /**
     * Bulk synchronize multiple seats
     */
    @Transactional
    fun syncSeats(seats: List<Seat>) {
        logger.info("Bulk synchronizing ${seats.size} seats")
        seatRepository.saveAll(seats)
    }

    /**
     * Listen for booking events to update seat availability
     */
    @KafkaListener(topics = ["booking-events"], groupId = "product-service")
    fun handleBookingEvent(bookingEvent: Map<String, Any>) {
        val eventType = bookingEvent["eventType"] as String
        
        when (eventType) {
            "SEAT_RESERVED", "BOOKING_CONFIRMED" -> {
                val seatIds = (bookingEvent["seatIds"] as List<*>).map { it as String }
                updateSeatAvailability(seatIds, false)
            }
            "BOOKING_CANCELLED" -> {
                val seatIds = (bookingEvent["seatIds"] as List<*>).map { it as String }
                updateSeatAvailability(seatIds, true)
            }
        }
    }
    
    private fun updateSeatAvailability(seatIds: List<String>, available: Boolean) {
        seatIds.forEach { seatId ->
            seatRepository.findById(seatId).ifPresent { seat ->
                val updatedSeat = seat.copy(available = available)
                seatRepository.save(updatedSeat)
                logger.info("Updated seat ${seatId} availability to $available")
            }
        }
    }
}
