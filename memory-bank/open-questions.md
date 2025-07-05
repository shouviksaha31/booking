# Open Questions

This document tracks open questions and decisions that need to be made for the Flight Booking Platform.

## Architecture Questions

### ✅ Q-001: Database Technology Selection
- **Question**: Should we use JPA/Hibernate or JOOQ for database access?
- **Status**: Resolved
- **Resolution**: JOOQ selected for type-safe SQL queries with better control and performance
- **Resolution Date**: 2025-07-05

### ✅ Q-002: Search Technology Selection
- **Question**: Should we use PostgreSQL full-text search or Elasticsearch for flight search?
- **Status**: Resolved
- **Resolution**: Elasticsearch selected for better search capabilities and performance
- **Resolution Date**: 2025-07-05

### ✅ Q-003: Messaging System Selection
- **Question**: Which messaging system should we use for event-driven communication?
- **Status**: Resolved
- **Resolution**: Apache Kafka selected for reliable event streaming
- **Resolution Date**: 2025-07-05

### ✅ Q-004: API Gateway vs. Central Orchestration
- **Question**: Should we use an API Gateway or a central orchestration service?
- **Status**: Resolved
- **Resolution**: Journey Management Service as central orchestrator for better workflow coordination
- **Resolution Date**: 2025-07-05

### ✅ Q-005: User Management Integration
- **Question**: Should we use an external user management system or build our own?
- **Status**: Resolved
- **Resolution**: Internal User Management Service for better control and customization
- **Resolution Date**: 2025-07-05

### ✅ Q-006: Product Service Database Strategy
- **Question**: Should Product Service use a single database or multiple databases?
- **Status**: Resolved
- **Resolution**: Dual database strategy - Elasticsearch for flights data, SQL for business data
- **Resolution Date**: 2025-07-05

### ✅ Q-007: Payment Processing Communication
- **Question**: Should payment processing use event-driven or direct communication?
- **Status**: Resolved
- **Resolution**: Hybrid approach - direct communication for processing, events for status updates
- **Resolution Date**: 2025-07-05

## Implementation Questions

### ❓ Q-008: Elasticsearch Mapping Design
- **Question**: How should we design the Elasticsearch mappings for optimal flight search?
- **Status**: Open
- **Options**:
  - Use nested objects for flight legs and seats
  - Use parent-child relationships
  - Use flattened structure with denormalization
- **Considerations**: Search performance, update complexity, query flexibility

### ❓ Q-009: Booking State Machine Implementation
- **Question**: How should we implement the booking state machine?
- **Status**: Open
- **Options**:
  - Spring State Machine
  - Custom implementation with enums
  - Database-driven state machine
- **Considerations**: Complexity, persistence, event handling

### ❓ Q-010: Payment Integration Strategy
- **Question**: Which payment providers should we integrate with initially?
- **Status**: Open
- **Options**:
  - Stripe
  - PayPal
  - Adyen
  - Multiple providers with adapter pattern
- **Considerations**: Market coverage, integration complexity, fees

### ❓ Q-011: Caching Strategy
- **Question**: What caching strategy should we implement for flight search?
- **Status**: Open
- **Options**:
  - Redis for distributed caching
  - Local cache with caffeine
  - No cache initially
- **Considerations**: Performance, consistency, complexity

### ❓ Q-012: API Versioning Strategy
- **Question**: How should we handle API versioning?
- **Status**: Open
- **Options**:
  - URL path versioning (e.g., /api/v1/...)
  - Header-based versioning
  - Content negotiation
- **Considerations**: Client compatibility, maintenance, documentation

### ❓ Q-013: Deployment Strategy
- **Question**: What deployment strategy should we use?
- **Status**: Open
- **Options**:
  - Kubernetes with Helm charts
  - Docker Compose for simpler deployments
  - Serverless for specific components
- **Considerations**: Scalability, complexity, operational overhead

### Change Log Entry [2025-07-05]
- **What**: Updated open questions document with resolved architecture questions and new implementation questions
- **Why**: To track decisions made and identify new questions that need resolution
- **Impact**: Provides clear view of resolved and open questions for project planning
- **Reference**: Architecture decisions and ProjectOverview.md
