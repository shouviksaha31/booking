plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.openapi.generator") version "7.1.0"
}

dependencies {
    // Depend on common module
    implementation(project(":common"))
    
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.16")
    implementation("io.swagger.core.v3:swagger-models:2.2.16")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // Kafka for event-driven communication
    implementation("org.springframework.kafka:spring-kafka")
    
    // Database
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:elasticsearch")
    testImplementation("org.testcontainers:kafka")
}

// OpenAPI Generator configuration
openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$projectDir/src/main/resources/openapi/product-service-api.yaml")
    outputDir.set("$buildDir/generated")
    apiPackage.set("com.windsurf.booking.product.api")
    modelPackage.set("com.windsurf.booking.product.api.model")
    configOptions.set(mapOf(
        "dateLibrary" to "java8",
        "delegatePattern" to "true",
        "useTags" to "true",
        "interfaceOnly" to "true",
        "useSpringBoot3" to "true",
        "documentationProvider" to "springdoc",
        "serializationLibrary" to "jackson"
    ))
}

// Add generated sources to the main source set
sourceSets {
    main {
        java {
            srcDir("${buildDir}/generated/src/main/kotlin")
        }
    }
}

// Make sure OpenAPI code is generated before compiling
tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
}
