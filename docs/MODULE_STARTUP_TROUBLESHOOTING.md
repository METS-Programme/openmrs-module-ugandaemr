# UgandaEMR Module Startup Troubleshooting Guide

## Date
2026-06-26

## Overview
This document provides troubleshooting guidance for common module startup issues in UgandaEMR, particularly when upgrading to platform 2.8.x.

---

## Quick Reference: SQL Cleanup Script
To quickly clean up orphaned scheduled tasks, run:
```bash
mysql -u your_user -p ugandaemr280 < tools/cleanup_orphaned_tasks.sql
```

Then restart the OpenMRS server.

---

## Issues Identified

### Issue 1: Filter Registration Error
```
java.lang.IllegalStateException: Filters cannot be added to context [/openmrs] as the context has been initialised
    at org.apache.catalina.core.ApplicationContext.checkState(ApplicationContext.java:1227)
    at org.apache.catalina.core.ApplicationContext.addFilter(ApplicationContext.java:730)
```

**Cause**: When starting a module through the Module Management UI after OpenMRS has already started, the servlet context is already initialized. The module startup process tries to refresh the Spring application context, which triggers filter registration - but this fails because Tomcat prevents adding filters after initialization.

**Location**: `WebComponentRegistrar.setServletContext()` at line 39

**Solution**: Restart the server instead of starting modules through the UI.

---

### Issue 2: Missing AtomFeed Module
```
ERROR - TaskFactory.createInstance(63) | OpenmrsClassLoader could not load class: org.openmrs.module.atomfeed.scheduler.tasks.EventPublisherTask. Probably due to a module not being loaded
ERROR - TimerSchedulerServiceImpl.scheduleTask(275) | Failed to schedule task OpenMRS event publisher task
java.lang.ClassNotFoundException: org.openmrs.module.atomfeed.scheduler.tasks.EventPublisherTask
```

**Cause**: The atomfeed module was recently removed from the distribution configuration:

Git diff from `api/src/main/resources/openmrs-distro.properties`:
```diff
-omod.openmrs-atomfeed.groupId=org.ict4h.openmrs
-omod.openmrs-atomfeed=${openmrsAtomfeedVersion}
```

However, the database still has a scheduled task referencing `org.openmrs.module.atomfeed.scheduler.tasks.EventPublisherTask`, which causes the ClassNotFoundException during module startup.

**Solution**: Run the cleanup SQL script to remove orphaned tasks.

---

### Issue 3: Data Import Failures

#### 3a. Duplicate Encounter Type
```
ERROR - VisitTypesInitializer.started(30) | Failed to import visit types metadata (non-critical, continuing)
java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'Early Infant Diagnosis (EID) Request' for key 'encounter_type.name'
```

**Cause**: The module initializer tries to import an encounter type that already exists in the database.

**Location**: `VisitTypesInitializer.started()` at line 30

**Solution**: The error is marked as non-critical. To fix permanently, update the initializer to check for existing data before inserting.

#### 3b. Foreign Key Constraint Violation
```
ERROR - OrderFrequenciesInitializer.started(30) | Failed to import order frequencies metadata
java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`ugandaemr280`.`order_frequency`, CONSTRAINT `order_frequency_concept_id_fk` FOREIGN KEY (`concept_id`) REFERENCES `concept` (`concept_id`) ON DELETE RESTRICT ON UPDATE RESTRICT)
```

**Cause**: The module initializer tries to import order frequencies that reference concepts that don't exist in your database.

**Location**: `OrderFrequenciesInitializer.started()` at line 30

**Solution**: Ensure required concepts are imported before running the module initializer.

---

### Issue 4: LinkExposedInfantToMotherTask
```
java.lang.ClassNotFoundException: org.openmrs.module.ugandaemr.tasks.LinkExposedInfantToMotherTask
```

**Cause**: This task was likely removed or refactored in the UgandaEMR module, but the database still has a scheduled task referencing it.

**Location**: Database `scheduler_task_config` table

**Solution**: Run the cleanup SQL script to remove orphaned tasks.

---

## Module Comparison Analysis

### Modules in pom.xml properties BUT NOT in runtime:
| Module | Version | Status |
|--------|---------|--------|
| dataintegrity | 4.4.4 | Not in distro.properties or runtime |
| formfilter | 1.3.0 | Not in distro.properties or runtime |
| ugandaemrreports | 2.1.6 | In distro.properties but NOT in runtime |
| databasebackup | 1.3.0 | Recently removed from distro |
| dataentrystatistics | 1.8.0 | In distro.properties but NOT in runtime |
| ugandaemrsync | 3.0.0 | In distro.properties but NOT in runtime |
| openmrs-atomfeed | 2.6.1 | Recently removed from distro |
| reportingcompatibility | 2.0.9 | In distro.properties but NOT in runtime |

### Modules in runtime BUT NOT in pom.xml:
| Module | Version | Source |
|--------|---------|--------|
| Authentication | 2.3.0 | Core module (bundled with platform) |
| Reference Demo Data Module | 2.6.1 | Core module |
| Patient Documents | 1.1.0 | Now added to pom.xml and distro |
| Tasks | 2.1.0 | Now added to pom.xml and distro |
| OpenMRS Billing Module | 2.3.0 | Not managed by this pom.xml |
| Bed Management Module | 7.2.0 | Not managed by this pom.xml |

---

## Solutions

### Solution 1: Remove Orphaned Scheduled Tasks

Run the SQL cleanup script:
```bash
mysql -u your_user -p ugandaemr280 < tools/cleanup_orphaned_tasks.sql
```

Or manually run these SQL commands:

```sql
-- Remove orphaned atomfeed task configurations
DELETE FROM scheduler_task_config_config
WHERE schedulable_class = 'org.openmrs.module.atomfeed.scheduler.tasks.EventPublisherTask';

DELETE FROM scheduler_task_config
WHERE schedulable_class = 'org.openmrs.module.atomfeed.scheduler.tasks.EventPublisherTask';

-- Remove orphaned LinkExposedInfantToMotherTask
DELETE FROM scheduler_task_config_config
WHERE schedulable_class = 'org.openmrs.module.ugandaemr.tasks.LinkExposedInfantToMotherTask';

DELETE FROM scheduler_task_config
WHERE schedulable_class = 'org.openmrs.module.ugandaemr.tasks.LinkExposedInfantToMotherTask';

-- Also check for any other atomfeed references
DELETE FROM scheduler_task_config_config
WHERE schedulable_class LIKE '%atomfeed%';

DELETE FROM scheduler_task_config
WHERE schedulable_class LIKE '%atomfeed%';
```

---

### Solution 2: Restart the Server Properly

The filter registration error happens because modules are being started through the UI after server startup. The proper approach:

**Option A: Full Server Restart**
1. Stop the OpenMRS server
2. Restart the server completely
3. Let all modules load during proper initialization

**Option B: Use OpenMRS SDK**
```bash
# From the module directory
mvn clean install
cd omod/target
mvn openmrs-sdk-maven-plugin:run
```

⚠️ **Do NOT start modules through the Module Management UI** after the server has already started, as this can cause filter registration issues.

---

### Solution 3: Fix Data Import Issues

#### For Duplicate Encounter Type:
The duplicate error is marked as "non-critical" in the code, so the module continues to start. To resolve:

```sql
-- Check if the encounter type already exists
SELECT encounter_type_id, name, description
FROM encounter_type
WHERE name = 'Early Infant Diagnosis (EID) Request';

-- If it exists and you want to use the existing one, no action needed
-- The module initializer needs to be updated to handle existing data
```

#### For Missing Order Frequency Concepts:
```sql
-- Find which concepts are missing
SELECT concept_id, uuid, name, retired
FROM concept
WHERE concept_id IN (
    SELECT DISTINCT concept_id
    FROM order_frequency
    WHERE concept_id NOT IN (SELECT concept_id FROM concept)
);

-- You may need to import the missing concepts first
-- Check the OrderFrequenciesInitializer data files for required concepts
```

---

### Solution 4: Update Module Initializers

The module initializers need to be updated to handle existing data gracefully:

**File**: `api/src/main/java/org/openmrs/module/ugandaemr/activator/VisitTypesInitializer.java`

**Current Issue**: Tries to insert without checking for duplicates

**Recommended Fix**:
```java
// Check if encounter type exists before inserting
EncounterType existing = Context.getEncounterService().getEncounterTypeByName(name);
if (existing == null) {
    // Import the data
} else {
    log.info("Encounter type '" + name + "' already exists, skipping import");
}
```

---

## Recommended Action Plan

1. **Immediate Actions**:
   - [ ] Run SQL to remove orphaned tasks: `tools/cleanup_orphaned_tasks.sql`
   - [ ] Restart the OpenMRS server completely
   - [ ] Verify module starts successfully

2. **Follow-up Actions**:
   - [ ] Update module initializers to handle existing data gracefully
   - [ ] Review and update the openmrs-distro.properties to ensure consistency
   - [ ] Consider adding a database migration script for cleanup

3. **Long-term Actions**:
   - [ ] Implement proper module lifecycle management
   - [ ] Add data validation checks during module initialization
   - [ ] Document module dependencies and startup order requirements

---

## Additional Notes

- The atomfeed module removal is intentional - the git history shows it was removed as part of the platform upgrade
- The patientflags module was added to replace some atomfeed functionality
- Patient Documents (1.1.0) and Tasks (2.1.0) modules have been added to the distribution
- Some modules are configured in pom.xml but not actively used - consider cleaning up unused dependencies
- The filter registration issue is a known limitation when starting modules dynamically through the UI

---

## Related Files

- `api/src/main/resources/openmrs-distro.properties` - Distribution module configuration
- `pom.xml` - Maven dependency management
- `api/src/main/java/org/openmrs/module/ugandaemr/activator/VisitTypesInitializer.java` - Visit types initialization
- `api/src/main/java/org/openmrs/module/ugandaemr/activator/OrderFrequenciesInitializer.java` - Order frequencies initialization
- `api/src/main/java/org/openmrs/module/ugandaemr/UgandaEMRActivator.java` - Main module activator
- `tools/cleanup_orphaned_tasks.sql` - SQL script for cleaning up orphaned scheduled tasks
