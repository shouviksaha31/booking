# System Patterns

This document outlines the architectural patterns and design principles used in the Flight Booking Platform.

## Architectural Patterns

### Microservices Architecture
The system follows a microservices architecture with the following key services:
- **Journey Management Service**: Central orchestration service that coordinates workflows and handles authentication
- **Product Service**: Manages product inventory using Elasticsearch for flights data and SQL database for business data
- **Booking Service**: Manages the booking process and state transitions
- **Payment Integration Service**: Integrates with external payment systems
- **Notification Service**: Handles notifications for booking state changes
- **User Management Service**: Handles user authentication, authorization, and profile management

### Event-Driven Architecture
The system uses Apache Kafka as an event bus for asynchronous communication between services:
- Services publish domain events when significant state changes occur
- Services subscribe to relevant events from other services
- Provides loose coupling between services
- Enables eventual consistency across the system

### Dual Database Pattern
The Product Service uses two different databases optimized for different use cases:
- **Elasticsearch**: For flight data that requires fast search capabilities
- **SQL Database**: For business data that requires transactional consistency

### Hybrid Communication Pattern
The system uses a combination of synchronous and asynchronous communication:
- **Synchronous**: Direct service-to-service calls for immediate responses (e.g., Booking Service to Payment Integration Service)
- **Asynchronous**: Event-driven communication for state updates and notifications

## Design Patterns

### Repository Pattern
- Each service implements repositories for data access
- Abstracts the underlying data storage technology
- Provides a consistent interface for data operations

### Factory Pattern
- Used for creating different types of products (flights, potentially movies in the future)
- Encapsulates the creation logic and provides a consistent interface

### Strategy Pattern
- Used for implementing different booking strategies based on product type
- Allows for extension to support new product types without modifying existing code

### State Machine Pattern
- Used for managing booking states and transitions
- Ensures that only valid state transitions are allowed
- Provides a clear model of the booking lifecycle

### Command Pattern
- Used for encapsulating payment requests as commands
- Enables features like retry, timeout, and compensation

## Layered Architecture

Each service follows a layered architecture:

### API Layer
- Handles HTTP requests and responses
- Input validation
- Authentication and authorization
- Rate limiting and throttling

### Service Layer
- Implements business logic
- Orchestrates operations across multiple repositories
- Manages transactions
- Publishes domain events

### Repository Layer
- Handles data access
- Abstracts underlying database technology
- Implements caching strategies

### Domain Model
- Represents core business entities and value objects
- Encapsulates business rules and invariants
- Provides a ubiquitous language for the domain

## Integration Patterns

### API Gateway Pattern
- Journey Management Service acts as an API gateway
- Provides a single entry point for client applications
- Handles cross-cutting concerns like authentication

### Circuit Breaker Pattern
- Used for handling failures in service-to-service communication
- Prevents cascading failures
- Provides fallback mechanisms

### Saga Pattern
- Used for managing distributed transactions across services
- Implements compensating transactions for rollback
- Ensures data consistency across services

## Extensibility Patterns

### Plugin Architecture
- Allows for adding new product types without modifying core code
- Uses interfaces and dependency injection for extensibility

### Feature Toggles
- Enables/disables features based on configuration
- Supports A/B testing and gradual rollout of features

## Monitoring and Observability

### Metrics Collection
- Each service publishes metrics for monitoring
- Tracks performance, errors, and business KPIs

### Distributed Tracing
- Traces requests across service boundaries
- Helps identify performance bottlenecks and errors

### Centralized Logging
- All services log to a centralized logging system
- Provides a unified view of system behavior

### Change Log Entry [2025-07-05]
- **What**: Updated system patterns document to reflect revised architecture
- **Why**: Reflect changes in system design and component interactions
- **Impact**: Provides understanding of system design and component interactions
- **Reference**: Project structure and build.gradle.kts analysis
