# UgandaEMR Module - Architecture Documentation

## System Overview

The UgandaEMR module is an OpenMRS extension module that enhances the core OpenMRS platform with specialized functionality for healthcare delivery in Uganda. The module follows OpenMRS's standard module architecture with clear separation of concerns across API, OMOD, and tool layers.

### Module Structure

```
openmrs-module-ugandaemr/
├── api/                    # Core business logic and data access
│   ├── src/main/java/      # Service interfaces and implementations
│   └── src/main/resources/ # Configuration files
├── omod/                   # Web application layer
│   ├── src/main/java/      # REST resources and web controllers
│   ├── src/main/webapp/    # Web UI resources
│   └── src/main/resources/ # OMOD-specific configuration
└── tools/                  # Standalone utilities and scripts
```

## Architectural Layers

### 1. API Layer (`api/`)

**Purpose:** Contains core business logic, service interfaces, and data access code.

**Key Components:**

- **Service Interfaces** (`org.openmrs.module.ugandaemr.api`)
  - `UgandaEMRService` - Main service interface
  - Defines contracts for business operations

- **Service Implementation** (`org.openmrs.module.ugandaemr.api.impl`)
  - `UgandaEMRServiceImpl` - Core business logic implementation
  - Handles patient management, laboratory services, pharmacy operations
  - Implements data access and transaction management

- **Domain Models** (`org.openmrs.module.ugandaemr.api.dto`)
  - Data Transfer Objects for API communication
  - `StabilityCriteria`, `Identifier`, `PatientQueue`

- **Exception Hierarchy** (`org.openmrs.module.ugandaemr.exception`)
  - `UgandaEMRException` - Base exception class
  - `PatientValidationException` - Patient-specific validation errors
  - `LaboratoryException` - Laboratory operation errors
  - `PharmacyException` - Pharmacy operation errors

- **Utility Classes** (`org.openmrs.module.ugandaemr.util`)
  - `ValidationUtil` - Input validation framework
  - `DateUtil` - Thread-safe date formatting
  - `PerformanceUtil` - Batch processing utilities
  - `AlgorithmUtil` - Algorithmic optimizations
  - `PerformanceMonitor` - Performance monitoring

- **Caching Layer** (`org.openmrs.module.ugandaemr.cache`)
  - `MetadataCache` - In-memory cache for frequently accessed metadata

- **Mapper Classes** (`org.openmrs.module.ugandaemr.mapper`)
  - `IdentifierMapperUtil` - Consolidated identifier mapping logic

### 2. OMOD Layer (`omod/`)

**Purpose:** Web application layer containing REST resources and web controllers.

**Key Components:**

- **REST Resources** (`org.openmrs.module.ugandaemr.web.resource`)
  - `PatientResource` - Patient management endpoints
  - `OrderResultResource` - Laboratory result management
  - `OrderObsResource` - Laboratory observation handling
  - `StabilityCriteriaResource` - Clinical decision support
  - `PharmacyResource` - Pharmacy operations
  - `QueueResource` - Patient queue management

- **Web Controllers** (`org.openmrs.module.ugandaemr.web.controller`)
  - HTTP request handling for web UI
  - Form processing and validation

- **Configuration**
  - `moduleApplicationContext.xml` - Spring bean configuration
  - `liquibase.xml` - Database schema migrations

### 3. Tools Layer (`tools/`)

**Purpose:** Standalone utilities and administrative scripts.

**Components:**
- Database migration tools
- Data import/export utilities
- Administrative scripts

## Data Flow Architecture

### Request Processing Flow

```
1. HTTP Request → REST Resource
2. REST Resource → Service Layer
3. Service Layer → DAO/Database
4. Database → Service Layer
5. Service Layer → REST Resource
6. REST Resource → HTTP Response
```

### Example: Patient Transfer Operation

```
REST Resource (PatientResource)
  ↓ Validates input
Service Layer (UgandaEMRServiceImpl)
  ↓ Business logic
  ↓ Transaction management
DAO Layer (Hibernate/OpenMRS API)
  ↓ Database Operation
Database (MySQL/PostgreSQL)
  ↓ Result
Service Layer
  ↓ Transform response
REST Resource
  ↓ Format response
HTTP Response
```

## Database Schema

### Core Tables

**Patient-Related Tables:**
- `patient` - Core patient data
- `patient_identifier` - Patient identifiers
- `patient_program` - Patient program enrollments

**Laboratory Tables:**
- `orders` - Laboratory orders
- `obs` - Laboratory observations
- `ugandaemr_lab_report` - Custom lab reports

**Pharmacy Tables:**
- `drug_order` - Medication orders
- `drug` - Drug inventory
- `ugandaemr_pharmacy_dispensing` - Dispensing records

**Queue Management:**
- `ugandaemr_patient_queue` - Patient queue entries
- `ugandaemr_queue_stats` - Queue statistics

### Database Access Patterns

**Hibernate ORM:**
- Primary data access mechanism
- Object-relational mapping for core entities
- Transaction management through OpenMRS API

**Native SQL:**
- Used for complex queries requiring optimization
- Parameterized queries to prevent SQL injection
- Batch operations for performance

**Liquibase Migrations:**
- Version-controlled database schema changes
- Rollback capabilities for schema modifications
- Applied automatically on module startup

## Service Layer Architecture

### Service Responsibilities

**UgandaEMRServiceImpl** implements:

1. **Patient Management**
   - Patient transfers (in/out)
   - Patient queue management
   - Patient validation

2. **Laboratory Services**
   - Test order processing
   - Result management
   - Accession number generation
   - Observation handling

3. **Pharmacy Operations**
   - Drug order processing
   - Dispensing validation
   - Drug interaction checking

4. **Clinical Decision Support**
   - Stability criteria evaluation
   - Clinical alerts generation
   - Care protocols

### Transaction Management

**OpenMRS Transaction Strategy:**
- Spring-managed transactions
- `@Transactional` annotation for service methods
- Database connection pooling through OpenMRS
- Transaction rollback on exceptions

## Caching Strategy

### Multi-Level Caching

**1. Metadata Cache (MetadataCache.java)**
- Concepts and encounter types
- 5-minute TTL
- ConcurrentHashMap for thread safety
- Automatic expiry cleanup

**2. OpenMRS Core Cache**
- Patient data caching
- Program enrollment cache
- Configured through OpenMRS admin

**Cache Invalidation:**
- Time-based expiration (TTL)
- Manual invalidation on metadata updates
- Memory-based storage

## Security Architecture

### Authentication & Authorization

**OpenMRS Integration:**
- Leverages OpenMRS authentication system
- Role-based access control (RBAC)
- Privilege-based operation authorization

**Security Layers:**
1. **Input Validation** (`ValidationUtil`)
   - UUID validation
   - Date range validation
   - Numeric range validation
   - SQL injection prevention

2. **SQL Injection Prevention**
   - Parameterized queries
   - Prepared statements
   - Input sanitization

3. **Output Encoding**
   - REST API response encoding
   - XSS prevention
   - Content-type headers

4. **Exception Handling**
   - Custom exception hierarchy
   - Secure error messages
   - Audit logging

## Performance Optimization

### Database Optimization

**Query Optimization:**
- Batch processing to eliminate N+1 queries
- Indexed columns for faster lookups
- Native SQL for complex operations

**Connection Management:**
- Connection pooling
- Proper resource cleanup
- Transaction management

### Application Optimization

**Algorithmic Improvements:**
- O(n²) to O(n) complexity reductions
- Stream-based operations
- Efficient data structures

**Memory Management:**
- Batch processing for large datasets
- Memory usage monitoring
- Resource cleanup

### Performance Monitoring

**PerformanceMonitor Utility:**
- Execution time tracking
- Slow operation logging
- Memory usage statistics
- Database query monitoring

## Integration Architecture

### OpenMRS Integration Points

**1. Module Context**
- Access to OpenMRS services
- Module configuration management
- Event subscription system

**2. Service Layer Integration**
- PatientService
- EncounterService
- ConceptService
- OrderService
- ProgramWorkflowService

**3. Data Model Extension**
- Custom fields through OpenMRS extensibility
- Custom tables for module-specific data
- Foreign keys to core OpenMRS tables

### External System Integration

**REST API:**
- Standardized REST endpoints
- JSON request/response format
- OpenMRS authentication

**Future Integration Points:**
- Laboratory information systems (LIS)
- Pharmacy management systems
- External health information exchanges

## Deployment Architecture

### Development Environment

**Local Development:**
- OpenMRS SDK for local development
- Embedded Jetty server
- H2/MySQL database

**Build Process:**
- Maven-based build system
- Automated testing
- Module packaging (`.omod` file)

### Production Environment

**Application Server:**
- Apache Tomcat
- OpenMRS 2.x platform
- MySQL/PostgreSQL database

**Deployment Process:**
1. Build module: `mvn clean install`
2. Deploy `.omod` file to OpenMRS
3. Restart OpenMRS application
4. Run Liquibase migrations
5. Configure module settings

**Scalability Considerations:**
- Stateless REST resources
- Database connection pooling
- Caching for frequently accessed data
- Load balancing support

## Configuration Management

### Module Configuration

**Config.xml Parameters:**
- Feature toggles
- Threshold values
- Integration endpoints
- Performance tuning parameters

**Environment-Specific Configuration:**
- Development settings
- Production settings
- Test environment settings

## Monitoring & Logging

### Logging Strategy

**Log Levels:**
- ERROR: Critical failures requiring immediate attention
- WARN: Performance issues and deprecated operations
- INFO: Important business operations
- DEBUG: Detailed troubleshooting information

**Log Categories:**
- Security events
- Performance metrics
- Business operations
- Error conditions

### Performance Monitoring

**Key Metrics:**
- Response times
- Database query performance
- Memory usage
- Cache hit rates
- Error rates

## Testing Architecture

### Test Structure

**Unit Tests:**
- Service layer testing
- Utility class testing
- Mock-based testing with Mockito

**Integration Tests:**
- End-to-end API testing
- Database integration testing
- REST resource testing

**Test Utilities:**
- TestDataFactory for test object creation
- Test data builders
- Test database setup

## Future Architecture Considerations

### Scalability Enhancements

1. **Service Decomposition**
   - Extract PatientTransferService
   - Extract LaboratoryService
   - Extract PharmacyService
   - Extract QueueManagementService

2. **Database Optimization**
   - Read replicas for reporting
   - Archive old data
   - Advanced indexing strategies

3. **Performance Improvements**
   - Distributed caching (Redis)
   - Asynchronous processing
   - Background job queues

### Integration Opportunities

1. **Health Information Exchange**
   - FHIR API implementation
   - HL7 messaging support
   - Standards-based interoperability

2. **Analytics & Reporting**
   - Data warehouse integration
   - Real-time analytics
   - Business intelligence tools

This architecture provides a solid foundation for the UgandaEMR module's current functionality while allowing for future growth and enhancement.