package com.windsurf.booking.product.repository.elasticsearch

import com.windsurf.booking.product.domain.model.Seat
import com.windsurf.booking.product.domain.model.SeatType
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

/**
 * Elasticsearch repository for Seat entity
 */
@Repository
interface SeatElasticsearchRepository : ElasticsearchRepository<Seat, String> {
    fun findByStopId(stopId: String): List<Seat>
    fun findByStopIdAndAvailable(stopId: String, available: Boolean): List<Seat>
    fun findByStopIdAndType(stopId: String, type: SeatType): List<Seat>
    fun findByStopIdAndTypeAndAvailable(stopId: String, type: SeatType, available: Boolean): List<Seat>
    fun countByStopIdAndAvailable(stopId: String, available: Boolean): Long
}
