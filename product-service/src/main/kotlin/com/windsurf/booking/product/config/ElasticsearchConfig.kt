package com.windsurf.booking.product.config

import org.elasticsearch.client.RestHighLevelClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@EnableElasticsearchRepositories(basePackages = ["com.windsurf.booking.product.repository.elasticsearch"])
class ElasticsearchConfig {

    @Bean
    fun client(): RestHighLevelClient {
        val clientConfiguration = ClientConfiguration.builder()
            .connectedTo("localhost:9200")
            .build()
        return RestClients.create(clientConfiguration).rest()
    }

    @Bean
    fun elasticsearchTemplate(): ElasticsearchOperations {
        return ElasticsearchRestTemplate(client())
    }
    
    @Bean
    fun indexInitializer(elasticsearchOperations: ElasticsearchOperations): IndexInitializer {
        return IndexInitializer(elasticsearchOperations)
    }
}

class IndexInitializer(private val elasticsearchOperations: ElasticsearchOperations) {
    
    private val indices = listOf(
        "flight-index.json",
        "stop-index.json",
        "seat-index.json"
    )
    
    fun initialize() {
        indices.forEach { indexFile ->
            val indexName = indexFile.substringBefore("-index.json")
            val resource = ClassPathResource("elasticsearch/$indexFile")
            val indexMapping = resource.inputStream.bufferedReader().use { it.readText() }
            
            val indexOps = elasticsearchOperations.indexOps(Class.forName("com.windsurf.booking.product.domain.model.${indexName.capitalize()}"))
            if (!indexOps.exists()) {
                indexOps.createWithMapping(indexMapping)
            }
        }
    }
}
