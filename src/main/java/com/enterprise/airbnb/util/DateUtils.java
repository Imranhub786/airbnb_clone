package com.enterprise.airbnb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for date and time operations
 */
@Component
public class DateUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
    
    // Common date formatters
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    
    /**
     * Get current date
     */
    public static LocalDate now() {
        return LocalDate.now();
    }
    
    /**
     * Get current date and time
     */
    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }
    
    /**
     * Get current time
     */
    public static LocalTime nowTime() {
        return LocalTime.now();
    }
    
    /**
     * Format date to string
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }
    
    /**
     * Format date and time to string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }
    
    /**
     * Format date for display
     */
    public static String formatDisplayDate(LocalDate date) {
        return date != null ? date.format(DISPLAY_DATE_FORMATTER) : null;
    }
    
    /**
     * Format date and time for display
     */
    public static String formatDisplayDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_DATETIME_FORMATTER) : null;
    }
    
    /**
     * Parse date from string
     */
    public static LocalDate parseDate(String dateString) {
        try {
            return dateString != null ? LocalDate.parse(dateString, DATE_FORMATTER) : null;
        } catch (Exception e) {
            logger.error("Error parsing date: {}", dateString, e);
            return null;
        }
    }
    
    /**
     * Parse date and time from string
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return dateTimeString != null ? LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER) : null;
        } catch (Exception e) {
            logger.error("Error parsing date time: {}", dateTimeString, e);
            return null;
        }
    }
    
    /**
     * Calculate days between two dates
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * Calculate hours between two date times
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }
    
    /**
     * Calculate minutes between two date times
     */
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }
    
    /**
     * Add days to date
     */
    public static LocalDate addDays(LocalDate date, long days) {
        return date != null ? date.plusDays(days) : null;
    }
    
    /**
     * Add hours to date time
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        return dateTime != null ? dateTime.plusHours(hours) : null;
    }
    
    /**
     * Add minutes to date time
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime != null ? dateTime.plusMinutes(minutes) : null;
    }
    
    /**
     * Check if date is in the past
     */
    public static boolean isPast(LocalDate date) {
        return date != null && date.isBefore(now());
    }
    
    /**
     * Check if date is in the future
     */
    public static boolean isFuture(LocalDate date) {
        return date != null && date.isAfter(now());
    }
    
    /**
     * Check if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(now());
    }
    
    /**
     * Check if date time is in the past
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(nowDateTime());
    }
    
    /**
     * Check if date time is in the future
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(nowDateTime());
    }
    
    /**
     * Check if two date ranges overlap
     */
    public static boolean dateRangesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
    
    /**
     * Get start of day
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }
    
    /**
     * Get end of day
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date != null ? date.atTime(LocalTime.MAX) : null;
    }
    
    /**
     * Get start of month
     */
    public static LocalDate startOfMonth(LocalDate date) {
        return date != null ? date.withDayOfMonth(1) : null;
    }
    
    /**
     * Get end of month
     */
    public static LocalDate endOfMonth(LocalDate date) {
        return date != null ? date.withDayOfMonth(date.lengthOfMonth()) : null;
    }
    
    /**
     * Get start of year
     */
    public static LocalDate startOfYear(LocalDate date) {
        return date != null ? date.withDayOfYear(1) : null;
    }
    
    /**
     * Get end of year
     */
    public static LocalDate endOfYear(LocalDate date) {
        return date != null ? date.withDayOfYear(date.lengthOfYear()) : null;
    }
    
    /**
     * Generate list of dates between two dates
     */
    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return dates;
        }
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        
        return dates;
    }
    
    /**
     * Convert LocalDate to epoch milliseconds
     */
    public static long toEpochMilli(LocalDate date) {
        return date != null ? date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0;
    }
    
    /**
     * Convert LocalDateTime to epoch milliseconds
     */
    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0;
    }
    
    /**
     * Convert epoch milliseconds to LocalDate
     */
    public static LocalDate fromEpochMilli(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    /**
     * Convert epoch milliseconds to LocalDateTime
     */
    public static LocalDateTime fromEpochMilliDateTime(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * Get age from birth date
     */
    public static int getAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return (int) ChronoUnit.YEARS.between(birthDate, now());
    }
    
    /**
     * Check if date is weekend
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) {
            return false;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
    
    /**
     * Check if date is weekday
     */
    public static boolean isWeekday(LocalDate date) {
        return !isWeekend(date);
    }
}



