# UgandaEMR Module - Developer Guide

## Getting Started

### Prerequisites

**Required Software:**
- Java Development Kit (JDK) 8 or higher
- Apache Maven 3.6+
- OpenMRS SDK 2.x
- Git
- IDE (IntelliJ IDEA recommended, Eclipse supported)

**Database:**
- MySQL 5.7+ or PostgreSQL 10+
- H2 database for development/testing

### Development Environment Setup

#### 1. Clone Repository

```bash
git clone https://github.com/openmrs/openmrs-module-ugandaemr.git
cd openmrs-module-ugandaemr
```

#### 2. Build Module

```bash
mvn clean install
```

This will:
- Compile all source code
- Run unit tests
- Package the module as `.omod` file
- Install to local Maven repository

#### 3. Setup OpenMRS SDK

```bash
# Install OpenMRS SDK
npm install -g openmrs-sdk

# Setup development environment
openmrs-sdk setup

# Deploy module to local OpenMRS
openmrs-sdk deploy
```

#### 4. Run Development Server

```bash
# Start OpenMRS with embedded Jetty
mvn openmrs-sdk:run

# Or deploy to external Tomcat
mvn package
# Copy target/*.omod to OpenMRS modules directory
```

## Project Structure

### Directory Layout

```
openmrs-module-ugandaemr/
├── api/                               # API Layer
│   ├── src/main/java/
│   │   └── org/openmrs/module/ugandaemr/
│   │       ├── api/                   # Service interfaces
│   │       ├── api/impl/              # Service implementations
│   │       ├── api/dto/               # Data Transfer Objects
│   │       ├── exception/             # Custom exceptions
│   │       ├── cache/                 # Caching layer
│   │       ├── mapper/                # Mapping utilities
│   │       └── util/                  # Utility classes
│   ├── src/main/resources/
│   │   └── liquibase.xml             # Database migrations
│   ├── src/test/java/                 # Unit tests
│   └── pom.xml                        # API module dependencies
├── omod/                              # OMOD Layer
│   ├── src/main/java/
│   │   └── org/openmrs/module/ugandaemr/
│   │       └── web/
│   │           ├── resource/          # REST resources
│   │           └── controller/        # Web controllers
│   ├── src/main/webapp/
│   │   └── resources/                 # Web resources
│   ├── src/main/resources/
│   │   └── moduleApplicationContext.xml
│   └── pom.xml                        # OMOD module dependencies
├── tools/                             # Standalone tools
├── docs/                              # Documentation
│   ├── API.md                         # API documentation
│   ├── ARCHITECTURE.md                # Architecture documentation
│   └── SECURITY.md                    # Security guidelines
├── pom.xml                            # Parent POM
└── README.md                          # Project overview
```

### Module Layers

**API Layer (`api/`):**
- Business logic and data access
- Service interfaces and implementations
- Domain models and DTOs
- Utility classes and helpers
- No web-specific code

**OMOD Layer (`omod/`):**
- REST resources and controllers
- Web UI components
- HTTP request handling
- Delegates business logic to API layer

**Tools (`tools/`):**
- Standalone utilities
- Administrative scripts
- Data migration tools

## Development Workflow

### Coding Standards

#### Java Code Style

**Naming Conventions:**
- Classes: `PascalCase` (e.g., `PatientService`)
- Methods: `camelCase` (e.g., `getPatientById`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)
- Packages: `lowercase` (e.g., `org.openmrs.module.ugandaemr.api`)

**Code Organization:**
- Keep methods under 50 lines
- Keep classes under 500 lines
- One class per file
- Package-private for internal methods

**Documentation:**
- Javadoc for public APIs
- Inline comments for complex logic
- Parameter descriptions
- Return value documentation

#### Code Quality Guidelines

**Write Clean Code:**
- Use meaningful names
- Keep methods small and focused
- Avoid code duplication
- Follow SOLID principles
- Use composition over inheritance

**Example:**
```java
// GOOD: Clear, focused method with validation
public Patient transferPatient(Integer patientId, Date transferDate) {
    ValidationUtil.validatePatientId(patientId);
    ValidationUtil.validateDate(transferDate);
    
    Patient patient = patientService.getPatient(patientId);
    validatePatientForTransfer(patient);
    
    return processTransfer(patient, transferDate);
}

// AVOID: Complex, multi-purpose method
public Object process(Object input, String type) {
    // Too many responsibilities
}
```

### Testing Standards

#### Unit Testing

**Requirements:**
- Test all public service methods
- Test edge cases and error conditions
- Use Mockito for dependencies
- Test data factories for complex objects

**Test Structure:**
```java
public class PatientServiceTest {
    
    @Mock
    private PatientDAO patientDAO;
    
    @InjectMocks
    private PatientServiceImpl patientService;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void shouldTransferPatientSuccessfully() {
        // Given
        Patient patient = TestDataFactory.createPatient();
        when(patientDAO.getById(1)).thenReturn(patient);
        
        // When
        Patient result = patientService.transferPatient(1, new Date());
        
        // Then
        assertNotNull(result);
        verify(patientDAO).save(any(Patient.class));
    }
    
    @Test(expected = PatientValidationException.class)
    public void shouldThrowExceptionForInvalidPatientId() {
        // When
        patientService.transferPatient(null, new Date());
        
        // Then exception expected
    }
}
```

#### Integration Testing

**REST Resource Testing:**
```java
public class PatientResourceTest {
    
    private MockHttpServletRequest request;
    private PatientResource resource;
    
    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        resource = new PatientResource();
        resource.setService(Context.getService(UgandaEMRService.class));
    }
    
    @Test
    public void shouldGetPatientById() throws Exception {
        // Given
        request.setParameter("patientId", "1");
        
        // When
        SimpleObject response = resource.retrieve(request, "1");
        
        // Then
        assertNotNull(response.get("uuid"));
        assertEquals(1, response.get("patientId"));
    }
}
```

#### Test Coverage Goals

- **Critical Business Logic:** 70%+ coverage
- **Service Layer:** 60%+ coverage
- **REST Resources:** 50%+ coverage
- **Utility Classes:** 80%+ coverage

### Security Best Practices

#### Input Validation

**Always Validate Input:**
```java
public void processSampleId(String sampleId) {
    // Validate UUID format
    if (!ValidationUtil.isValidUUID(sampleId)) {
        throw new IllegalArgumentException("Invalid sample ID format");
    }
    
    // Validate length
    if (sampleId.length() > 100) {
        throw new IllegalArgumentException("Sample ID too long");
    }
    
    // Process validated input
    processValidatedSampleId(sampleId);
}
```

#### SQL Injection Prevention

**Use Parameterized Queries:**
```java
// GOOD: Parameterized query
Query query = session.createQuery(
    "FROM Patient p WHERE p.uuid = :uuid");
query.setParameter("uuid", patientUuid);

// BAD: String concatenation (SQL injection risk)
String sql = "FROM Patient p WHERE p.uuid = '" + patientUuid + "'";
```

#### Exception Handling

**Custom Exceptions:**
```java
// Use specific exceptions
throw new PatientValidationException("PATIENT_NOT_FOUND", 
    "Patient not found with ID: " + patientId, patientId);

// Avoid generic exceptions
throw new RuntimeException("Error"); // Not recommended
```

### Performance Guidelines

#### Database Optimization

**Avoid N+1 Queries:**
```java
// BAD: N+1 query problem
for (Integer orderId : orderIds) {
    Order order = orderService.getOrder(orderId);
    processOrder(order);
}

// GOOD: Batch processing
Map<Integer, Order> orderMap = PerformanceUtil.getOrdersBatch(orderIds);
for (Order order : orderMap.values()) {
    processOrder(order);
}
```

#### Caching Strategy

**Use Caching for Frequently Accessed Data:**
```java
public Concept getConceptByUuid(String uuid) {
    // Check cache first
    Concept cached = MetadataCache.getConcept(uuid);
    if (cached != null) {
        return cached;
    }
    
    // Load from database
    Concept concept = conceptService.getConceptByUuid(uuid);
    if (concept != null) {
        MetadataCache.putConcept(uuid, concept);
    }
    return concept;
}
```

## Build and Deployment

### Local Development

**Quick Build:**
```bash
mvn clean install
```

**Skip Tests (development only):**
```bash
mvn clean install -DskipTests
```

**Run Specific Test:**
```bash
mvn test -Dtest=PatientServiceTest
```

### Release Process

#### Version Management

**Update Version in pom.xml:**
```xml
<groupId>org.openmrs.module</groupId>
<artifactId>ugandaemr</artifactId>
<version>1.0.0</version> <!-- Update this -->
```

**Create Release Branch:**
```bash
git checkout -b release/1.0.0
```

#### Build Release

```bash
# Clean build
mvn clean install

# Run full test suite
mvn verify

# Create release tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

#### Deploy to Maven Repository

```bash
# Deploy to local repository
mvn clean install

# Deploy to remote repository (requires credentials)
mvn deploy
```

### Deployment to Production

#### Pre-Deployment Checklist

- [ ] All tests passing
- [ ] Code review completed
- [ ] Documentation updated
- [ ] Security scan passed
- [ ] Performance tested
- [ ] Database migrations tested

#### Deployment Steps

1. **Backup Database:**
   ```bash
   mysqldump -u root -p openmrs > backup.sql
   ```

2. **Deploy Module:**
   ```bash
   # Stop OpenMRS
   sudo systemctl stop openmrs
   
   # Copy new module
   cp target/ugandaemr-1.0.0.omd /var/lib/OpenMRS/modules/
   
   # Start OpenMRS
   sudo systemctl start openmrs
   ```

3. **Verify Deployment:**
   - Check OpenMRS logs for errors
   - Verify module is loaded
   - Test critical functionality
   - Monitor performance metrics

## Troubleshooting

### Common Issues

#### Build Failures

**Problem:** Compilation errors
```bash
# Clean and rebuild
mvn clean install -U

# Check Java version
java -version  # Should be 1.8+
```

**Problem:** Test failures
```bash
# Run tests with verbose output
mvn test -X

# Skip failing tests (temporary)
mvn install -DskipTests
```

#### Runtime Issues

**Problem:** Module not loading
```bash
# Check OpenMRS logs
tail -f /var/lib/OpenMRS/logs/openmrs.log

# Verify module dependencies
mvn dependency:tree
```

**Problem:** Database connection issues
```bash
# Check database connectivity
mysql -u root -p openmrs

# Verify connection settings in openmrs-runtime.properties
```

### Debugging

#### Enable Debug Logging

**Configure Logback:**
```xml
<!-- In omod/src/main/resources/logback.xml -->
<logger name="org.openmrs.module.ugandaemr" level="DEBUG"/>
<logger name="org.hibernate.SQL" level="DEBUG"/>
```

#### Remote Debugging

**Start OpenMRS with Debug:**
```bash
mvn openmrs-sdk:run -Ddebug=true
```

**Connect IDE Debugger:**
- Host: localhost
- Port: 5005

## Code Review Process

### Pull Request Guidelines

#### Before Submitting PR

1. **Code Quality:**
   - Follow coding standards
   - Add/update tests
   - Update documentation
   - No compiler warnings

2. **Testing:**
   - All tests passing
   - New tests added for new features
   - Manual testing completed

3. **Documentation:**
   - Javadoc for public APIs
   - Update README if needed
   - Add examples for complex features

#### Pull Request Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Performance improvement
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests passing
- [ ] Manual testing completed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No new warnings generated
```

### Review Process

1. **Automated Checks:**
   - Build verification
   - Test execution
   - Code coverage

2. **Peer Review:**
   - Code quality assessment
   - Architecture review
   - Security review

3. **Approval:**
   - At least one approval required
   - All review comments addressed
   - Tests passing

## Contributing

### Contribution Guidelines

#### Ways to Contribute

1. **Bug Reports:**
   - Use GitHub Issues
   - Include steps to reproduce
   - Provide environment details
   - Add relevant logs

2. **Feature Requests:**
   - Describe use case
   - Propose implementation approach
   - Consider impact on existing features

3. **Code Contributions:**
   - Fork repository
   - Create feature branch
   - Write tests
   - Submit pull request

#### Feature Development

**1. Proposal:**
- Open issue describing feature
- Discuss implementation approach
- Get community feedback

**2. Development:**
- Create feature branch: `git checkout -b feature/your-feature`
- Implement feature with tests
- Update documentation

**3. Review:**
- Submit pull request
- Address review feedback
- Ensure tests pass

**4. Merge:**
- Get approval
- Squash commits if needed
- Merge to master
- Delete feature branch

### Community Guidelines

#### Code of Conduct

- Be respectful and constructive
- Welcome new contributors
- Focus on what is best for the community
- Show empathy towards other community members

#### Communication Channels

- GitHub Issues: Bug reports and feature requests
- GitHub Discussions: General questions
- OpenMRS Talk: Community forum
- Slack: Real-time collaboration

## Additional Resources

### Documentation

- [OpenMRS Documentation](https://wiki.openmrs.org/)
- [OpenMRS Module Development](https://wiki.openmrs.org/display/docs/Module+Documentation)
- [REST API Documentation](./docs/API.md)
- [Architecture Documentation](./docs/ARCHITECTURE.md)

### Development Tools

- [OpenMRS SDK](https://wiki.openmrs.org/display/docs/OpenMRS+SDK)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Hibernate Documentation](https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/)
- [Spring Framework](https://spring.io/guides)

### Testing Resources

- [JUnit 4 Documentation](https://junit.org/junit4/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [TestNG Documentation](https://testng.org/doc/)

## Support

### Getting Help

- **Documentation:** Start with module documentation
- **OpenMRS Wiki:** General OpenMRS questions
- **GitHub Issues:** Bug reports and feature requests
- **Community Forums:** General questions and discussions

### Reporting Issues

When reporting issues, include:

1. Environment details:
   - OpenMRS version
   - Module version
   - Java version
   - Database type and version

2. Steps to reproduce

3. Expected vs actual behavior

4. Error messages and stack traces

5. Relevant configuration

Thank you for contributing to the UgandaEMR module!