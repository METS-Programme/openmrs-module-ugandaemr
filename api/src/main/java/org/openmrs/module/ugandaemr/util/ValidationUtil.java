package org.openmrs.module.ugandaemr.util;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.util.OpenmrsUtil;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility class for input validation to prevent security vulnerabilities
 * including SQL injection, XSS, and data integrity issues.
 */
public class ValidationUtil {

    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    private static final Pattern SAMPLE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]+$");

    private static final Pattern ALPHANUMERIC_SAFE_PATTERN = Pattern.compile("^[a-zA-Z0-9 _.-]+$");

    /**
     * Validates if a string is a valid UUID format.
     *
     * @param uuid the UUID string to validate
     * @return true if valid UUID format, false otherwise
     */
    public static boolean isValidUUID(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return false;
        }
        return UUID_PATTERN.matcher(uuid).matches();
    }

    /**
     * Validates if a string is a valid sample ID format.
     * Sample IDs should contain only alphanumeric characters and hyphens.
     *
     * @param sampleId the sample ID to validate
     * @return true if valid sample ID format, false otherwise
     */
    public static boolean isValidSampleId(String sampleId) {
        if (StringUtils.isBlank(sampleId)) {
            return false;
        }
        return SAMPLE_ID_PATTERN.matcher(sampleId).matches() && sampleId.length() <= 100;
    }

    /**
     * Validates if a string is safe for use in database queries.
     * Allows only alphanumeric characters, spaces, and common safe characters.
     *
     * @param input the input string to validate
     * @return true if safe, false otherwise
     */
    public static boolean isSafeString(String input) {
        if (StringUtils.isBlank(input)) {
            return false;
        }
        return ALPHANUMERIC_SAFE_PATTERN.matcher(input).matches() && input.length() <= 255;
    }

    /**
     * Validates if a date is within a reasonable range.
     *
     * @param date the date to validate
     * @param minYearsBefore minimum years before current date (e.g., 120 for birth dates)
     * @param maxYearsAfter maximum years after current date (e.g., 5 for future appointments)
     * @return true if date is within range, false otherwise
     */
    public static boolean isValidDateRange(Date date, int minYearsBefore, int maxYearsAfter) {
        if (date == null) {
            return false;
        }

        Date now = new Date();
        long minTime = now.getTime() - (minYearsBefore * 365L * 24 * 60 * 60 * 1000);
        long maxTime = now.getTime() + (maxYearsAfter * 365L * 24 * 60 * 60 * 1000);

        return date.getTime() >= minTime && date.getTime() <= maxTime;
    }

    /**
     * Validates if a numeric value is within a specified range.
     *
     * @param value the numeric value to validate
     * @param min minimum acceptable value
     * @param max maximum acceptable value
     * @return true if within range, false otherwise
     */
    public static boolean isValidNumericRange(Double value, double min, double max) {
        if (value == null) {
            return false;
        }
        return value >= min && value <= max;
    }

    /**
     * Validates if a numeric value is within a specified range.
     *
     * @param value the numeric value to validate
     * @param min minimum acceptable value
     * @param max maximum acceptable value
     * @return true if within range, false otherwise
     */
    public static boolean isValidNumericRange(Integer value, int min, int max) {
        if (value == null) {
            return false;
        }
        return value >= min && value <= max;
    }

    /**
     * Sanitizes input by removing potentially dangerous characters.
     * This is a basic sanitization - proper validation should still be performed.
     *
     * @param input the input string to sanitize
     * @return sanitized string
     */
    public static String sanitizeInput(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        // Remove common SQL injection patterns
        return input.replaceAll("([';--|/\\*])", "");
    }

    /**
     * Validates UUID and throws IllegalArgumentException if invalid.
     *
     * @param uuid the UUID to validate
     * @param paramName the parameter name for error message
     * @throws IllegalArgumentException if UUID is invalid
     */
    public static void requireValidUUID(String uuid, String paramName) {
        if (!isValidUUID(uuid)) {
            throw new IllegalArgumentException(paramName + " must be a valid UUID format");
        }
    }

    /**
     * Validates sample ID and throws IllegalArgumentException if invalid.
     *
     * @param sampleId the sample ID to validate
     * @throws IllegalArgumentException if sample ID is invalid
     */
    public static void requireValidSampleId(String sampleId) {
        if (!isValidSampleId(sampleId)) {
            throw new IllegalArgumentException("Sample ID must contain only alphanumeric characters and hyphens, and be 100 characters or less");
        }
    }

    /**
     * Validates that a required string is not blank.
     *
     * @param value the string value to validate
     * @param paramName the parameter name for error message
     * @throws IllegalArgumentException if string is blank
     */
    public static void requireNotBlank(String value, String paramName) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(paramName + " cannot be blank");
        }
    }

    /**
     * Validates that a required object is not null.
     *
     * @param value the object to validate
     * @param paramName the parameter name for error message
     * @throws IllegalArgumentException if object is null
     */
    public static void requireNotNull(Object value, String paramName) {
        if (value == null) {
            throw new IllegalArgumentException(paramName + " cannot be null");
        }
    }
}