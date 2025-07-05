package com.windsurf.booking.common.test

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ClassPathResource
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

/**
 * Common test utilities for all services
 */
object TestUtils {
    /**
     * Generates a random UUID string for testing
     */
    fun randomId(): String = UUID.randomUUID().toString()
    
    /**
     * Loads a test resource file as a string
     */
    fun loadResourceAsString(path: String): String {
        return ClassPathResource(path).inputStream.bufferedReader().use { it.readText() }
    }
}

/**
 * Configuration for fixing the clock in tests
 */
@TestConfiguration
class TestClockConfiguration {
    @Bean
    @Primary
    fun fixedClock(): Clock {
        return Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneId.of("UTC"))
    }
}
