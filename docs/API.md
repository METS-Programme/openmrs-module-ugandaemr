# UgandaEMR REST API Documentation

## Overview

The UgandaEMR module provides comprehensive REST API endpoints for patient management, laboratory operations, pharmacy services, and clinical workflows. This documentation covers all available endpoints, request/response formats, authentication, and usage examples.

**Base URL**: `/ws/rest/v1/ugandaemr`

**API Version**: v1.0

**Content Type**: `application/json`

## Authentication

All API endpoints require OpenMRS authentication. Requests must include valid authentication credentials.

### Authentication Methods

1. **Basic Authentication**: Include username/password in request headers
2. **Session Authentication**: Use existing OpenMRS session cookies
3. **API Key Authentication**: Include API key in request headers

### Authorization

Access to API endpoints is controlled by OpenMRS privilege system. Users must have appropriate privileges to access specific resources.

### Required Privileges

- **Patient Management**: View Patients, Edit Patients
- **Laboratory Services**: View Laboratory Results, Edit Laboratory Results
- **Pharmacy Services**: View Drug Orders, Edit Drug Orders
- **System Administration**: System Administration privileges for some operations

---

## Core Endpoints

### Patient Management Endpoints

#### Check-In Patient

**Endpoint**: `POST /ws/rest/v1/ugandaemr/checkinpatient`

**Description**: Checks in a patient to a specific location and queue room.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patient` | String | Yes | Patient UUID or ID |
| `currentLocation` | String | Yes | Current location UUID |
| `locationTo` | String | Yes | Target location UUID |
| `queueRoom` | String | Yes | Queue room UUID |
| `provider` | String | No | Provider UUID |
| `visitComment` | String | No | Comment for the visit |
| `patientStatus` | String | No | Patient status |
| `visitTypeUuid` | String | No | Visit type UUID |
| `priority` | Integer | No | Priority level (1-5) |

**Response**:
```json
{
  "uuid": "checkin-uuid",
  "patient": {
    "uuid": "patient-uuid",
    "display": "John Doe (Patient ID: 123)"
  },
  "location": "location-uuid",
  "queueNumber": 5,
  "visit": {
    "uuid": "visit-uuid",
    "visitType": "Outpatient"
  },
  "status": "checked-in"
}
```

**Error Responses**:
- `400 Bad Request`: Invalid parameters
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient privileges
- `404 Not Found`: Patient or location not found

---

#### Generate Sample ID

**Endpoint**: `POST /ws/rest/v1/ugandaemr/generatesampleid`

**Description**: Generates a laboratory sample ID for a specific order.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `orderUuid` | String | Yes | Order UUID to generate sample ID for |

**Response**:
```json
{
  "sampleId": "LAB-123-2024-04-19",
  "orderUuid": "order-uuid",
  "generated": true
}
```

**Error Responses**:
- `400 Bad Request`: Invalid order UUID
- `404 Not Found`: Order not found
- `409 Conflict`: Sample ID already exists

---

### Laboratory Service Endpoints

#### Laboratory Results

**Endpoint**: `GET /ws/rest/v1/ugandaemr/orderresult`

**Description**: Retrieves laboratory test results for a patient encounter.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `encounter` | String | Yes | Encounter UUID |
| `includeProcessed` | Boolean | No | Include processed results (default: false) |

**Response**:
```json
{
  "orders": [
    {
      "orderUuid": "order-uuid",
      "orderNumber": "ORD-12345",
      "testName": "CD4 Count",
      "sampleId": "LAB-123-2024-04-19",
      "result": "Not Detected",
      "resultDate": "2024-04-19T10:30:00+00:00",
      "status": "complete"
    }
  ]
}
```

---

#### Accession Lab Test

**Endpoint**: `POST /ws/rest/v1/ugandaemr/accessionlabtest`

**Description**: Accessions a laboratory test with sample information.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `orderUuid` | String | Yes | Order UUID to accession |
| `accessionNumber` | String | Yes | Laboratory sample ID |
| `specimenSourceUuid` | String | Yes | Specimen source concept UUID |
| `instructions` | String | No | Additional instructions |

**Response**:
```json
{
  "orderUuid": "order-uuid",
  "accessionNumber": "ACCN-001",
  "status": "in-progress",
  "accessionedDate": "2024-04-19T10:30:00+00:00"
}
```

---

#### Generate Lab Number

**Endpoint**: `POST /ws/rest/v1/ugandaemr/generatelabnumber`

**Description**: Generates a unique laboratory number for a test order.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `orderUuid` | String | Yes | Order UUID |

**Response**:
```json
{
  "labNumber": "LAB-123-19-04-2024",
  "orderUuid": "order-uuid",
  "generatedDate": "2024-04-19T10:30:00+00:00"
}
```

---

### Pharmacy Service Endpoints

#### Pharmacy Queue Mapping

**Endpoint**: `GET /ws/rest/v1/ugandaemr/pharmacyqueuemapper`

**Description**: Retrieves pharmacy queue information with drug orders for patients.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `includeOrders` | Boolean | No | Include drug order information (default: true) |

**Response**:
```json
{
  "queue": [
    {
      "patient": {
        "uuid": "patient-uuid",
        "display": "Jane Doe (Patient ID: 456)"
      },
      "location": "pharmacy-location-uuid",
      "queueNumber": 3,
      "drugOrders": [
        {
          "drugName": "TDF-3TC 300mg/600mg",
          "dose": "300mg",
          "frequency": "Twice daily",
          "duration": "30 days",
          "status": "active"
        }
      ]
    }
  ]
}
```

---

### Order Observation Endpoints

#### Order Observations

**Endpoint**: `GET /ws/rest/v1/ugandaemr/orderobs`

**Description**: Retrieves order observations for laboratory tests.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `encounter` | String | No | Encounter UUID to filter observations |
| `onOrBefore` | Date | No | Filter observations before this date |
| `onOrAfter` | Date | No | Filter observations after this date |
| `includeVoided` | Boolean | No | Include voided observations (default: false) |

**Response**:
```json
{
  "observations": [
    {
      "uuid": "obs-uuid",
      "orderUuid": "order-uuid",
      "concept": {
        "uuid": "concept-uuid",
        "display": "CD4 Count"
      },
      "value": "Not Detected",
      "obsDateTime": "2024-04-19T10:30:00+00:00",
      "status": "final"
    }
  ]
}
```

---

#### Create Order Observation

**Endpoint**: `POST /ws/rest/v1/ugandaemr/orderobs`

**Description**: Creates an observation for a laboratory test order.

**Request Body**:
```json
{
  "orderUuid": "order-uuid",
  "conceptUuid": "concept-uuid",
  "value": "Detected",
  "obsDateTime": "2024-04-19T10:30:00+00:00"
}
```

**Response**:
```json
{
  "uuid": "created-obs-uuid",
  "orderUuid": "order-uuid",
  "status": "created"
}
```

---

### Queue Management Endpoints

#### Queue Statistics

**Endpoint**: `GET /ws/rest/v1/ugandaemr/queuestatistics`

**Description**: Retrieves statistics for patient queues by location tags.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `parentLocation` | String | Yes | Parent location UUID |
| `fromDate` | Date | No | Start date for statistics (format: yyyy-MM-dd) |
| `toDate` | Date | No | End date for statistics (format: yyyy-MM-dd) |
| `onlyInQueueRooms` | Boolean | No | Only include queue rooms (default: false) |

**Response**:
```json
{
  "statistics": [
    {
      "locationTag": {
        "uuid": "tag-uuid",
        "display": "Pharmacy"
      },
      "pending": 5,
      "serving": 2,
      "completed": 15
    }
  ]
}
```

---

#### Incomplete Patient Queue

**Endpoint**: `GET /ws/rest/v1/ugandaemr/incompletepatientqueue`

**Description**: Retrieves patients with incomplete queue records.

**Response**:
```json
{
  "incompleteQueues": [
    {
      "patient": {
        "uuid": "patient-uuid",
        "display": "John Doe (Patient ID: 789)"
      },
      "location": "location-uuid",
      "queueStatus": "pending",
      "lastUpdated": "2024-04-19T10:30:00+00:00"
    }
  ]
}
```

---

### Clinical Decision Support Endpoints

#### Stability Criteria

**Endpoint**: `GET /ws/rest/v1/ugandaemr/stabilitycriteria`

**Description**: Calculates clinical stability criteria for HIV patients based on their treatment history and laboratory results.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patient` | String | No | Patient UUID |
| `encounter` | String | No | Encounter UUID |
| `visit` | String | No | Visit UUID |

**Response**:
```json
{
  "uuid": "stability-uuid",
  "vlObs": {
    "uuid": "vl-obs-uuid",
    "valueNumeric": 750,
    "concept": {
      "uuid": "concept-uuid",
      "display": "HIV Viral Load"
    }
  },
  "artStartDate": "2020-01-15T00:00:00+00:00",
  "onThirdRegimen": false,
  "adherenceObs": [
    {
      "uuid": "adherence-obs-uuid",
      "valueCoded": {
        "uuid": "concept-uuid",
        "display": "Good"
      }
    }
  ],
  "enableCliniciansMakeStabilityDecisions": "true"
}
```

**Represents**:
- `DEFAULT`: Basic stability information
- `FULL`: Complete details with all observations
- `REF`: Reference to patient, encounter, visit

---

### Data Management Endpoints

#### Public Holidays

**Endpoint**: `/ws/rest/v1/ugandaemr/publicholiday`

**Description**: Manages public holiday configuration for the system.

**Methods**: GET, POST, DELETE (Standard CRUD operations)

**Public Holiday Object**:
```json
{
  "uuid": "holiday-uuid",
  "name": "Independence Day",
  "holidayDate": "2024-10-09T00:00:00+00:00",
  "description": "National Independence Day"
}
```

---

#### Duplicate Encounters

**Endpoint**: `GET /ws/rest/v1/ugandaemr/duplicateencounter`

**Description**: Identifies and manages duplicate patient encounters.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patient` | String | No | Patient UUID to check for duplicates |
| `encounterType` | String | No | Encounter type UUID to filter duplicates |

**Response**:
```json
{
  "duplicateEncounters": [
    {
      "encounterUuid": "encounter-uuid",
      "encounterDate": "2024-04-19T10:30:00+00:00",
      "encounterType": "Consultation",
      "duplicateReason": "Same patient, same date, same type"
    }
  ]
}
```

---

### System Management Endpoints

#### Global Properties

**Endpoint**: `/ws/rest/v1/ugandaemr/gp`

**Description**: Retrieves global configuration properties.

**Request Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `property` | String | Yes | Global property name |

**Response**:
```json
{
  "property": "ugandaemr.defaultLocation",
  "value": "location-uuid",
  "description": "Default facility location"
}
```

---

## Error Handling

### Standard Error Response Format

All error responses follow this format:

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "details": "Additional error details"
  }
}
```

### Common HTTP Status Codes

| Status Code | Meaning | Example Scenarios |
|-------------|---------|-------------------|
| `200 OK` | Success | Request completed successfully |
| `201 Created` | Resource Created | New patient check-in created |
| `400 Bad Request` | Invalid Input | Missing required parameters, invalid UUID format |
| `401 Unauthorized` | Authentication Required | No valid authentication provided |
| `403 Forbidden` | Insufficient Privileges | User lacks required privileges |
| `404 Not Found` | Resource Not Found | Patient, location, or order not found |
| `409 Conflict` | Resource Conflict | Sample ID already exists, duplicate encounter |
| `500 Internal Server Error` | Server Error | Unexpected server error |

### UgandaEMR-Specific Error Codes

| Error Code | Description | HTTP Status |
|------------|-------------|-------------|
| `PATIENT_VALIDATION_ERROR` | Patient data validation failed | 400 |
| `LABORATORY_ERROR` | Laboratory operation failed | 400 |
| `PHARMACY_ERROR` | Pharmacy operation failed | 400 |
| `DUPLICATE_RESOURCE` | Attempt to create duplicate resource | 409 |
| `RESOURCE_NOT_FOUND` | Requested resource not found | 404 |
| `INVALID_UUID_FORMAT` | Provided UUID format is invalid | 400 |
| `INSUFFICIENT_PRIVILEGES` | User lacks required privileges | 403 |

---

## Request/Response Examples

### Check-In Patient Example

**Request**:
```bash
curl -X POST "http://localhost:8080/ws/rest/v1/ugandaemr/checkinpatient" \
  -H "Content-Type: application/json" \
  -u admin:admin \
  -d '{
    "patient": "patient-uuid-here",
    "currentLocation": "current-location-uuid",
    "locationTo": "pharmacy-uuid",
    "queueRoom": "pharmacy-queue-uuid",
    "provider": "provider-uuid",
    "visitComment": "Routine pharmacy visit",
    "patientStatus": "active",
    "visitTypeUuid": "visit-type-uuid",
    "priority": 3
  }'
```

**Response**:
```json
{
  "uuid": "checkin-uuid",
  "patient": {
    "uuid": "patient-uuid-here",
    "display": "John Doe (Patient ID: 123)"
  },
  "location": "pharmacy-uuid",
  "queueNumber": 3,
  "status": "checked-in"
}
```

---

### Laboratory Results Example

**Request**:
```bash
curl -X GET "http://localhost:8080/ws/rest/v1/ugandaemr/orderresult?encounter=encounter-uuid&includeProcessed=true" \
  -u admin:admin
```

**Response**:
```json
{
  "orders": [
    {
      "orderUuid": "order-uuid",
      "orderNumber": "ORD-12345",
      "testName": "CD4 Count",
      "sampleId": "LAB-123-2024-04-19",
      "result": "750 copies/mL",
      "resultDate": "2024-04-19T10:30:00+00:00",
      "status": "complete"
    }
  ]
}
```

---

### Stability Criteria Example

**Request**:
```bash
curl -X GET "http://localhost:8080/ws/rest/v1/ugandaemr/stabilitycriteria?patient=patient-uuid&v=full" \
  -u admin:admin
```

**Response**:
```json
{
  "uuid": "stability-uuid",
  "vlObs": {
    "uuid": "vl-obs-uuid",
    "valueNumeric": 750,
    "concept": {
      "uuid": "concept-uuid",
      "display": "HIV Viral Load"
    },
    "obsDateTime": "2024-04-19T10:30:00+00:00"
  },
  "artStartDate": "2020-01-15T00:00:00+00:00",
  "regimenObs": {
    "uuid": "regimen-obs-uuid",
    "valueCoded": {
      "uuid": "concept-uuid",
      "display": "TDF-3TC+3TC+EFV"
    }
  },
  "onThirdRegimen": false,
  "enableCliniciansMakeStabilityDecisions": "true"
}
```

---

## Rate Limiting

API endpoints are subject to rate limiting to ensure fair usage and system stability.

### Rate Limits

- **Authenticated Requests**: 1000 requests per hour per user
- **Unauthenticated Requests**: 100 requests per hour per IP
- **Batch Operations**: 100 requests per hour per user

### Rate Limit Headers

Rate limit information is included in response headers:

```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 995
X-RateLimit-Reset: 1618826900
```

### Rate Limit Exceeded Response

When rate limits are exceeded, the API returns:

```json
{
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Rate limit exceeded. Please try again later.",
    "retryAfter": 3600
  }
}
```

---

## Data Types and Formats

### Date Formats

All dates use ISO 8601 format:
- **Date Only**: `yyyy-MM-dd` (e.g., `2024-04-19`)
- **DateTime**: `yyyy-MM-dd'T'HH:mm:ss.SSSZ` (e.g., `2024-04-19T10:30:00.000Z`)

### UUID Format

All UUIDs follow standard UUID format:
- **Format**: `xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx`
- **Example**: `877169c4-92c6-4cc9-bf45-1ab95faea242`

### Enumerations

Common status values:
- **Queue Status**: `pending`, `picked`, `completed`
- **Order Status**: `active`, `discontinued`, `completed`, `expired`
- **Visit Status**: `active`, `completed`, `cancelled`

---

## Pagination

Some endpoints support pagination for large result sets.

### Pagination Parameters

| Parameter | Type | Default | Description |
|-----------|------|--------|-------------|
| `startIndex` | Integer | 0 | Starting index |
| `limit` | Integer | 100 | Maximum results per page |

### Pagination Response

```json
{
  "results": [...],
  "totalCount": 250,
  "startIndex": 0,
  "limit": 100,
  "hasMoreResults": true
}
```

---

## Security Considerations

### Input Validation

- All UUIDs are validated for correct format before processing
- Input strings are sanitized to prevent injection attacks
- Numeric values are validated for appropriate ranges
- Date values are validated for reasonable ranges

### Output Encoding

- All output is properly encoded to prevent XSS attacks
- User-generated content is sanitized before return
- Sensitive information is filtered based on user privileges

### Audit Logging

All API operations are logged for audit purposes:
- User authentication details
- Request parameters
- Response status
- Timestamp

---

## Testing the API

### Using curl

```bash
# Check in patient
curl -X POST "http://localhost:8080/ws/rest/v1/ugandaemr/checkinpatient" \
  -H "Content-Type: application/json" \
  -u admin:admin \
  -d '{"patient":"patient-uuid","currentLocation":"location-uuid","locationTo":"pharmacy-uuid","queueRoom":"queue-uuid"}'

# Get laboratory results
curl -X GET "http://localhost:8080/ws/rest/v1/ugandaemr/orderresult?encounter=encounter-uuid" \
  -u admin:admin

# Get stability criteria
curl -X GET "http://localhost:8080/ws/rest/v1ugandaemr/stabilitycriteria?patient=patient-uuid" \
  -u admin:admin
```

### Using Swagger UI

Access the Swagger UI at: `http://localhost:8080/openmrs/module/ws/rest/v1/swagger`

---

## Version History

### Version 5.0.2 (Current)
- Added stability criteria endpoint
- Enhanced error handling with specific error codes
- Improved input validation and sanitization
- Added comprehensive caching for performance
- Optimized database queries to eliminate N+1 problems

### Version 5.0.1
- Added laboratory accession endpoints
- Enhanced queue management features
- Improved error responses

### Version 5.0.0
- Initial release with core UgandaEMR functionality

---

## Support and Documentation

For more information:
- **Architecture Documentation**: See [ARCHITECTURE.md](ARCHITECTURE.md)
- **Development Guide**: See [DEVELOPMENT.md](DEVELOPMENT.md)
- **Security Guidelines**: See [SECURITY.md](SECURITY.md)
- **OpenMRS API Documentation**: [OpenMRS REST API Docs](https://rest.openmrs.org/)

---

## Changelog

### Recent Updates (April 2024)
- **Added**: Performance monitoring endpoints
- **Enhanced**: Caching infrastructure for better response times
- **Fixed**: N+1 query problems in batch operations
- **Improved**: Error messages with specific error codes
- **Optimized**: Database query performance for large datasets

### Upcoming Features
- Enhanced batch operation endpoints
- Real-time notification support
- Advanced filtering and search capabilities
- Extended reporting and analytics endpoints