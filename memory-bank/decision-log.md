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

### AD-013: Multi-Project Gradle Structure
- **Date**: 2025-07-05
- **Decision**: Implement a multi-project Gradle structure with common, product-service, and booking-service modules
- **Rationale**: Promotes code reuse, clear separation of concerns, and modular development
- **Alternatives Considered**: Monolithic application, separate repositories for each service
- **Consequences**: Better code organization, easier dependency management, potential for shared code reuse

### AD-014: Dual Repository Pattern for Product Service
- **Date**: 2025-07-05
- **Decision**: Implement both JOOQ repositories for SQL data and Elasticsearch repositories for flight data
- **Rationale**: Leverages the strengths of both systems - SQL for transactional data and Elasticsearch for search
- **Alternatives Considered**: Using only SQL with full-text search, using only Elasticsearch
- **Consequences**: Better performance for different use cases, increased complexity in data synchronization

### AD-015: Data Synchronization Service
- **Date**: 2025-07-05
- **Decision**: Create a dedicated service for synchronizing data between SQL and Elasticsearch
- **Rationale**: Ensures data consistency across both databases while maintaining separation of concerns
- **Alternatives Considered**: Database triggers, application-level synchronization in repository methods
- **Consequences**: Better separation of concerns, potential for asynchronous updates, additional complexity

### AD-016: OpenAPI-First Approach
- **Date**: 2025-07-05
- **Decision**: Use OpenAPI-first approach for API development with code generation
- **Rationale**: Ensures consistent API design, provides automatic documentation, and reduces boilerplate code
- **Alternatives Considered**: Code-first approach with annotations, manual API documentation
- **Consequences**: Better API consistency, reduced development time, potential limitations in complex scenarios

### AD-017: Multi-Project Gradle Structure
- **Date**: 2025-07-05
- **Decision**: Use Gradle multi-project structure with Kotlin DSL
- **Rationale**: Provides better organization of code, reuse of common components, and simplified dependency management
- **Alternatives Considered**: Single project structure, Maven multi-module
- **Consequences**: Better code organization, simplified dependency management, more complex build configuration

### AD-018: Dual Repository Pattern
- **Date**: 2025-07-05
- **Decision**: Use dual repository pattern with PostgreSQL for transactional data and Elasticsearch for search
- **Rationale**: Leverages the strengths of both databases - ACID transactions in PostgreSQL and search capabilities in Elasticsearch
- **Alternatives Considered**: Single database with search extensions, microservice per database type
- **Consequences**: Better performance for different use cases, more complex data synchronization, additional infrastructure requirements

### AD-019: Data Synchronization Service
- **Date**: 2025-07-05
- **Decision**: Implement a dedicated data synchronization service to keep PostgreSQL and Elasticsearch in sync
- **Rationale**: Ensures data consistency between databases while decoupling the synchronization logic from business logic
- **Alternatives Considered**: Database triggers, application-level synchronization in repositories
- **Consequences**: Better separation of concerns, more complex architecture, additional component to maintain

### AD-020: Delegate Pattern for Controllers
- **Date**: 2025-07-05
- **Decision**: Use delegate pattern for controllers with service interfaces
- **Rationale**: Separates API contract from implementation, enables better testing, and provides clearer separation of concerns
- **Alternatives Considered**: Direct implementation in controllers, service layer without interfaces
- **Consequences**: Better testability, clearer separation of concerns, additional interfaces to maintain

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

### Change Log Entry [2025-07-05]
- **What**: Added new architectural decisions to the decision log
- **Why**: To maintain a comprehensive record of project decisions and their rationale
- **Impact**: Provides up-to-date information on project architecture and technology choices
- **Reference**: Recent project developments
