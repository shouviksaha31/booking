# Decision Log

This file tracks key architectural and technical decisions made during the project.

## Architectural Decisions

### AD-001: Use of JOOQ for Database Access
- **Date**: 2025-07-05
- **Decision**: Use JOOQ for type-safe SQL queries instead of JPA/Hibernate
- **Rationale**: JOOQ provides type safety and better control over SQL queries while avoiding the complexity of ORM
- **Alternatives Considered**: Hibernate, Spring Data JPA, MyBatis
- **Consequences**: Better SQL control, compile-time query validation, steeper learning curve for developers unfamiliar with JOOQ

### AD-002: Use of Flyway for Database Migrations
- **Date**: 2025-07-05
- **Decision**: Use Flyway for database schema migrations
- **Rationale**: Provides version control for database schema, easy rollback, and consistent database state across environments
- **Alternatives Considered**: Liquibase, manual SQL scripts
- **Consequences**: Better tracking of database changes, consistent deployment process, additional configuration required

### AD-003: Use of Elasticsearch for Flight Data
- **Date**: 2025-07-05
- **Decision**: Use Elasticsearch as the primary database for flight data
- **Rationale**: Provides fast search capabilities with complex filtering, which is essential for flight search functionality
- **Alternatives Considered**: PostgreSQL with full-text search, MongoDB
- **Consequences**: Better search performance, more complex setup, additional infrastructure requirements

### AD-004: Use of Kafka for Event-Driven Architecture
- **Date**: 2025-07-05
- **Decision**: Use Apache Kafka for event-driven communication between services
- **Rationale**: Provides reliable, scalable, and durable message delivery for asynchronous communication
- **Alternatives Considered**: RabbitMQ, ActiveMQ, direct service-to-service communication
- **Consequences**: Better scalability, more complex setup, additional infrastructure requirements

### AD-005: Journey Management Service as Orchestration Layer
- **Date**: 2025-07-05
- **Decision**: Use Journey Management Service as the central orchestration service instead of API Gateway
- **Rationale**: Provides better control over the booking workflow and state transitions
- **Alternatives Considered**: API Gateway pattern, direct service-to-service communication
- **Consequences**: Clearer separation of concerns, centralized orchestration, potential bottleneck if not scaled properly

### AD-006: Notification Service as Event Consumer Only
- **Date**: 2025-07-05
- **Decision**: Implement Notification Service as a pure event consumer without direct APIs
- **Rationale**: Simplifies the architecture by making notification delivery fully asynchronous and decoupled
- **Alternatives Considered**: Exposing direct APIs for notification management
- **Consequences**: Better separation of concerns, simplified notification service, potential challenges in tracking notification status

### AD-007: Database Structure Aligned with Domain Models
- **Date**: 2025-07-05
- **Decision**: Use domain model IDs as primary keys in database tables
- **Rationale**: Maintains consistency between domain models and database schema, simplifies mapping
- **Alternatives Considered**: Using separate technical IDs for database tables
- **Consequences**: Better alignment between domain and persistence layers, simpler code, potential challenges with ID generation

### AD-008: State Machine for Booking Flow
- **Date**: 2025-07-05
- **Decision**: Implement a comprehensive state machine for booking flow with explicit states and transitions
- **Rationale**: Provides clear control over the booking lifecycle and prevents invalid state transitions
- **Alternatives Considered**: Simple status flags, ad-hoc state management
- **Consequences**: Better control over booking flow, explicit validation of transitions, more complex implementation

### AD-009: Internal User Management Service
- **Date**: 2025-07-05
- **Decision**: Implement an internal User Management Service instead of using an external system
- **Rationale**: Provides full control over user authentication, authorization, and profile management
- **Alternatives Considered**: External identity provider, authentication as part of another service
- **Consequences**: More development work, better customization and control

### AD-010: Direct Communication for Payment Processing
- **Date**: 2025-07-05
- **Decision**: Implement direct communication between Booking Service and Payment Integration Service
- **Rationale**: Provides immediate response for payment processing while maintaining event-driven updates
- **Alternatives Considered**: Purely event-driven communication
- **Consequences**: Better user experience for payment processing, hybrid communication pattern

### AD-011: Dual Database Strategy for Product Service
- **Date**: 2025-07-05
- **Decision**: Use Elasticsearch for flight data and SQL database for business data in Product Service
- **Rationale**: Optimizes for different query patterns - fast searching for flights and transactional consistency for business data
- **Alternatives Considered**: Single database for all data
- **Consequences**: Better performance for different use cases, more complex data synchronization

### AD-012: Microservices Architecture
- **Date**: 2025-07-05
- **Decision**: Implement a microservices architecture with separate services for different domains
- **Rationale**: Provides better scalability, maintainability, and allows different teams to work independently
- **Alternatives Considered**: Monolithic architecture, modular monolith
- **Consequences**: Better scalability and team autonomy, more complex deployment and monitoring

### Change Log Entry [2025-07-05]
- **What**: Initial file creation
- **Why**: Setting up memory bank structure
- **Impact**: Establishes foundation for tracking project decisions
- **Reference**: Initial setup

### Change Log Entry [2025-07-05]
- **What**: Documented key architectural decisions based on project dependencies
- **Why**: To record technology choices and their rationale
- **Impact**: Provides context for technology decisions and guides future development
- **Reference**: build.gradle.kts analysis

### Change Log Entry [2025-07-05]
- **What**: Updated decision log with latest architectural decisions
- **Why**: To reflect current project architecture and technology choices
- **Impact**: Provides comprehensive overview of project decisions and their rationale
- **Reference**: Project architecture review

### Change Log Entry [2025-07-05]
- **What**: Added recent architectural decisions to the decision log
- **Why**: To maintain a comprehensive record of project decisions and their rationale
- **Impact**: Provides up-to-date information on project architecture and technology choices
- **Reference**: Recent project developments
