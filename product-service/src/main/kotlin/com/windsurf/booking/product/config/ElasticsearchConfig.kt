package com.windsurf.booking.product.config

import com.windsurf.booking.product.domain.model.Flight
import com.windsurf.booking.product.domain.model.Seat
import com.windsurf.booking.product.domain.model.Stop
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import jakarta.annotation.PostConstruct

@Configuration
@EnableElasticsearchRepositories(basePackages = ["com.windsurf.booking.product.repository.elasticsearch"])
class ElasticsearchConfig : ElasticsearchConfiguration() {

    override fun clientConfiguration() = org.springframework.data.elasticsearch.client.ClientConfiguration.builder()
        .connectedTo("localhost:9200")
        .build()
    
    @Bean
    fun elasticsearchClient(): ElasticsearchClient {
        val restClient = RestClient.builder(HttpHost("localhost", 9200)).build()
        val transport = RestClientTransport(restClient, JacksonJsonpMapper())
        return ElasticsearchClient(transport)
    }
    
    @Bean
    fun elasticsearchTemplate(): ElasticsearchOperations {
        return ElasticsearchTemplate(elasticsearchClient())
    }
    
    @Bean
    fun indexInitializer(elasticsearchOperations: ElasticsearchOperations): IndexInitializer {
        return IndexInitializer(elasticsearchOperations)
    }
}

class IndexInitializer(private val elasticsearchOperations: ElasticsearchOperations) {
    
    private val indexMappings = mapOf(
        "flight" to Flight::class.java,
        "stop" to Stop::class.java,
        "seat" to Seat::class.java
    )
    
    @PostConstruct
    fun initialize() {
        indexMappings.forEach { (indexName, entityClass) ->
            val indexFile = "$indexName-index.json"
            try {
                val resource = ClassPathResource("elasticsearch/$indexFile")
                val indexMapping = resource.inputStream.bufferedReader().use { it.readText() }
                
                val indexOps = elasticsearchOperations.indexOps(entityClass)
                if (!indexOps.exists()) {
                    indexOps.createWithMapping()
                }
            } catch (e: Exception) {
                println("Error initializing index for $indexName: ${e.message}")
            }
        }
    }
}
