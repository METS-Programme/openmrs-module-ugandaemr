-- UgandaEMR Database Cleanup Script
-- Purpose: Remove orphaned scheduled tasks from removed modules
-- Date: 2026-06-26
--
-- This script cleans up orphaned scheduler tasks that reference modules
-- that have been removed from the distribution (e.g., atomfeed module)
--
-- Usage: Run this SQL script against your OpenMRS database
--
-- WARNING: Always backup your database before running cleanup scripts!

-- =====================================================================
-- Part 1: Remove orphaned AtomFeed module tasks
-- =====================================================================

-- Check what orphaned tasks exist before deletion
SELECT
    task_config_id,
    name,
    schedulable_class,
    start_time,
    repeat_interval
FROM scheduler_task_config
WHERE schedulable_class LIKE '%atomfeed%';

-- Remove task configurations for orphaned atomfeed tasks
DELETE FROM scheduler_task_config_config
WHERE schedulable_class = 'org.openmrs.module.atomfeed.scheduler.tasks.EventPublisherTask';

-- Remove the orphaned tasks
DELETE FROM scheduler_task_config
WHERE schedulable_class = 'org.openmrs.module.atomfeed.scheduler.tasks.EventPublisherTask';

-- Clean up any other atomfeed references
DELETE FROM scheduler_task_config_config
WHERE schedulable_class LIKE '%atomfeed%';

DELETE FROM scheduler_task_config
WHERE schedulable_class LIKE '%atomfeed%';

-- =====================================================================
-- Part 2: Remove orphaned UgandaEMR LinkExposedInfantToMotherTask
-- =====================================================================

-- Check what LinkExposedInfantToMotherTask exists before deletion
SELECT
    task_config_id,
    name,
    schedulable_class,
    start_time,
    repeat_interval
FROM scheduler_task_config
WHERE schedulable_class = 'org.openmrs.module.ugandaemr.tasks.LinkExposedInfantToMotherTask';

-- Remove task configurations for orphaned LinkExposedInfantToMotherTask
DELETE FROM scheduler_task_config_config
WHERE schedulable_class = 'org.openmrs.module.ugandaemr.tasks.LinkExposedInfantToMotherTask';

DELETE FROM scheduler_task_config
WHERE schedulable_class = 'org.openmrs.module.ugandaemr.tasks.LinkExposedInfantToMotherTask';

-- =====================================================================
-- Part 3: Check for other orphaned tasks from removed modules
-- =====================================================================

-- List all scheduled tasks for review
SELECT
    task_config_id,
    name,
    schedulable_class,
    start_time,
    repeat_interval
FROM scheduler_task_config
ORDER BY schedulable_class;

-- =====================================================================
-- Part 4: Verify cleanup
-- =====================================================================

-- Verify no orphaned atomfeed tasks remain
SELECT COUNT(*) as orphaned_atomfeed_count
FROM scheduler_task_config
WHERE schedulable_class LIKE '%atomfeed%';

-- Verify no orphaned LinkExposedInfantToMotherTask remains
SELECT COUNT(*) as orphaned_link_infant_count
FROM scheduler_task_config
WHERE schedulable_class = 'org.openmrs.module.ugandaemr.tasks.LinkExposedInfantToMotherTask';

-- Show remaining active tasks (excluding cleaned up tasks)
SELECT
    task_config_id,
    name,
    schedulable_class,
    start_time,
    repeat_interval
FROM scheduler_task_config
WHERE schedulable_class NOT LIKE '%atomfeed%'
AND schedulable_class != 'org.openmrs.module.ugandaemr.tasks.LinkExposedInfantToMotherTask'
ORDER BY name;

-- =====================================================================
-- Additional Notes
-- =====================================================================

-- After running this script:
-- 1. Restart the OpenMRS server
-- 2. Check the startup logs for any remaining task-related errors
-- 3. Verify all required modules are loading correctly
--
-- If you still see errors about missing task classes, check the
-- scheduler_task_config table for any other modules that may have been removed.
--
-- Tasks cleaned up by this script:
-- 1. org.openmrs.module.atomfeed.scheduler.tasks.EventPublisherTask (removed module)
-- 2. org.openmrs.module.ugandaemr.tasks.LinkExposedInfantToMotherTask (removed/refactored)

-- Commit the changes (uncomment if using transactions)
-- COMMIT;
