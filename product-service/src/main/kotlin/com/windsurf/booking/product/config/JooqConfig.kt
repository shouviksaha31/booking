package com.windsurf.booking.product.config

import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import javax.sql.DataSource

@Configuration
class JooqConfig {

    @Bean
    fun connectionProvider(dataSource: DataSource) =
        DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))

    @Bean
    fun dsl(dataSource: DataSource): DefaultDSLContext {
        val jooqConfiguration = DefaultConfiguration().apply {
            set(connectionProvider(dataSource))
            set(SQLDialect.POSTGRES)
        }
        return DefaultDSLContext(jooqConfiguration)
    }
}
