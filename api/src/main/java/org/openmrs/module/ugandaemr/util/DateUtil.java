package org.openmrs.module.ugandaemr.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for date formatting and manipulation.
 * Provides thread-safe date operations and common date patterns used throughout UgandaEMR.
 */
public class DateUtil {

    // Common date format patterns
    public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String LAB_NUMBER_FORMAT = "dd-MM-yyyy";
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";

    // Common time constants
    public static final String DAY_START_TIME = "00:00:00";
    public static final String DAY_END_TIME = "23:59:59";
    public static final String NOON_TIME = "12:00:00";

    // Thread-local date formatters for thread safety
    private static final ThreadLocal<SimpleDateFormat> dateTimeFormatter =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_TIME_FORMAT));

    private static final ThreadLocal<SimpleDateFormat> isoDateTimeFormatter =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(ISO_DATE_TIME_FORMAT));

    private static final ThreadLocal<SimpleDateFormat> dateFormatter =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_FORMAT));

    private static final ThreadLocal<SimpleDateFormat> isoDateFormatter =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(ISO_DATE_FORMAT));

    private static final ThreadLocal<SimpleDateFormat> labNumberFormatter =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(LAB_NUMBER_FORMAT));

    private static final ThreadLocal<SimpleDateFormat> yearMonthFormatter =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(YEAR_MONTH_FORMAT));

    /**
     * Formats a date to standard date/time format.
     *
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return dateTimeFormatter.get().format(date);
    }

    /**
     * Formats a date to ISO date/time format.
     *
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatISODateTime(Date date) {
        if (date == null) {
            return null;
        }
        return isoDateTimeFormatter.get().format(date);
    }

    /**
     * Formats a date to standard date format.
     *
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormatter.get().format(date);
    }

    /**
     * Formats a date to ISO date format.
     *
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatISODate(Date date) {
        if (date == null) {
            return null;
        }
        return isoDateFormatter.get().format(date);
    }

    /**
     * Formats a date for lab number generation.
     *
     * @param date the date to format
     * @return formatted date string suitable for lab numbers
     */
    public static String formatForLabNumber(Date date) {
        if (date == null) {
            return null;
        }
        return labNumberFormatter.get().format(date);
    }

    /**
     * Formats a date to year-month format.
     *
     * @param date the date to format
     * @return formatted year-month string
     */
    public static String formatYearMonth(Date date) {
        if (date == null) {
            return null;
        }
        return yearMonthFormatter.get().format(date);
    }

    /**
     * Formats a date with a specific time.
     *
     * @param date the date to format
     * @param time the time to append (format: HH:mm:ss)
     * @return formatted date string
     */
    public static String formatDateTime(Date date, String time) {
        if (date == null) {
            return null;
        }
        String dateStr = isoDateFormatter.get().format(date);
        return dateStr + " " + (time != null ? time : DAY_START_TIME);
    }

    /**
     * Parses a date string to a Date object.
     *
     * @param dateStr the date string to parse
     * @param format the format pattern
     * @return parsed Date object
     * @throws ParseException if parsing fails
     */
    public static Date parseDate(String dateStr, String format) throws ParseException {
        if (dateStr == null || format == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(dateStr);
    }

    /**
     * Gets a date before or after the reference date by specified amounts.
     *
     * @param referenceDate the reference date
     * @param noOfDays number of days to add (negative to subtract)
     * @param noOfMonths number of months to add (negative to subtract)
     * @param noOfYears number of years to add (negative to subtract)
     * @return calculated date
     */
    public static Date getDateOffset(Date referenceDate, int noOfDays, int noOfMonths, int noOfYears) {
        if (referenceDate == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(referenceDate);

        if (noOfDays != 0) {
            cal.add(Calendar.DAY_OF_MONTH, noOfDays);
        }
        if (noOfMonths != 0) {
            cal.add(Calendar.MONTH, noOfMonths);
        }
        if (noOfYears != 0) {
            cal.add(Calendar.YEAR, noOfYears);
        }

        return cal.getTime();
    }

    /**
     * Gets a date before the reference date (for backward date calculations).
     *
     * @param referenceDate the reference date
     * @param noOfMonths number of months to go back
     * @param noOfYears number of years to go back
     * @return calculated date
     */
    public static Date getDateBefore(Date referenceDate, int noOfMonths, int noOfYears) {
        return getDateOffset(referenceDate, 0, -noOfMonths, -noOfYears);
    }

    /**
     * Gets a date after the reference date (for forward date calculations).
     *
     * @param referenceDate the reference date
     * @param noOfMonths number of months to go forward
     * @param noOfYears number of years to go forward
     * @return calculated date
     */
    public static Date getDateAfter(Date referenceDate, int noOfMonths, int noOfYears) {
        return getDateOffset(referenceDate, 0, noOfMonths, noOfYears);
    }

    /**
     * Checks if a date is between two other dates (inclusive).
     *
     * @param date the date to check
     * @param startDate the start date
     * @param endDate the end date
     * @return true if date is between start and end (inclusive)
     */
    public static boolean isDateBetween(Date date, Date startDate, Date endDate) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }

        return !date.before(startDate) && !date.after(endDate);
    }

    /**
     * Gets the start of day (midnight) for a given date.
     *
     * @param date the date
     * @return date representing start of day
     */
    public static Date getStartOfDay(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Gets the end of day (23:59:59.999) for a given date.
     *
     * @param date the date
     * @return date representing end of day
     */
    public static Date getEndOfDay(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTime();
    }

    /**
     * Calculates the difference in days between two dates.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return difference in days
     */
    public static long getDaysDifference(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }

        long diffInMillies = endDate.getTime() - startDate.getTime();
        return diffInMillies / (24 * 60 * 60 * 1000);
    }

    /**
     * Checks if a date is today.
     *
     * @param date the date to check
     * @return true if date is today
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }

        Date today = new Date();
        return formatDate(date).equals(formatDate(today));
    }
}