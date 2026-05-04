package org.openmrs.module.ugandaemr.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for monitoring and optimizing performance.
 * Provides methods for tracking operation times and identifying bottlenecks.
 */
public class PerformanceMonitor {

    private static final Log log = LogFactory.getLog(PerformanceMonitor.class);

    private static final long SLOW_OPERATION_THRESHOLD_MS = 1000; // 1 second
    private static final long VERY_SLOW_OPERATION_THRESHOLD_MS = 5000; // 5 seconds

    /**
     * Monitors the execution time of an operation.
     * Logs warnings for slow operations.
     *
     * @param operationName the name of the operation
     * @param operation the operation to monitor
     * @return the result of the operation
     */
    public static <T> T monitor(String operationName, ThrowingSupplier<T> operation) {
        long startTime = System.currentTimeMillis();
        try {
            T result = operation.get();
            long duration = System.currentTimeMillis() - startTime;

            if (duration > VERY_SLOW_OPERATION_THRESHOLD_MS) {
                log.warn("⚠️ VERY SLOW OPERATION: " + operationName + " took " + duration + "ms");
            } else if (duration > SLOW_OPERATION_THRESHOLD_MS) {
                log.warn("⚠️ SLOW OPERATION: " + operationName + " took " + duration + "ms");
            } else {
                log.debug("✅ " + operationName + " completed in " + duration + "ms");
            }

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("❌ Operation " + operationName + " failed after " + duration + "ms", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Monitors an operation that doesn't return a value.
     *
     * @param operationName the name of the operation
     * @param operation the operation to monitor
     */
    public static void monitorVoid(String operationName, ThrowingRunnable operation) {
        long startTime = System.currentTimeMillis();
        try {
            operation.run();
            long duration = System.currentTimeMillis() - startTime;

            if (duration > VERY_SLOW_OPERATION_THRESHOLD_MS) {
                log.warn("⚠️ VERY SLOW OPERATION: " + operationName + " took " + duration + "ms");
            } else if (duration > SLOW_OPERATION_THRESHOLD_MS) {
                log.warn("⚠️ SLOW OPERATION: " + operationName + " took " + duration + "ms");
            } else {
                log.debug("✅ " + operationName + " completed in " + duration + "ms");
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("❌ Operation " + operationName + " failed after " + duration + "ms", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Functional interface for operations that throw exceptions.
     *
     * @param <T> the return type
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    /**
     * Functional interface for runnable operations that throw exceptions.
     */
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    /**
     * Measures the execution time of an operation.
     *
     * @param operationName the name of the operation
     * @param operation the operation to measure
     * @return execution time in milliseconds
     */
    public static long measure(String operationName, ThrowingRunnable operation) {
        long startTime = System.currentTimeMillis();
        try {
            operation.run();
            long duration = System.currentTimeMillis() - startTime;
            log.debug("⏱️ " + operationName + " took " + duration + "ms");
            return duration;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("❌ Operation " + operationName + " failed after " + duration + "ms", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Formats a duration in human-readable format.
     *
     * @param milliseconds duration in milliseconds
     * @return formatted duration string
     */
    public static String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        } else if (milliseconds < 60000) {
            return String.format("%.2fs", milliseconds / 1000.0);
        } else {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    /**
     * Estimates memory usage for an object.
     *
     * @param object the object to measure
     * @return estimated size in bytes
     */
    public static long estimateObjectSize(Object object) {
        if (object == null) {
            return 0;
        }

        // Rough estimation based on Java object overhead
        // This is a simplified version - for accurate measurements, use Java Agent or instrumentation
        long size = 16; // Object header

        if (object instanceof String) {
            String str = (String) object;
            size += str.length() * 2; // Each char is 2 bytes
        } else if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            size += 32; // Collection overhead
            for (Object item : collection) {
                size += estimateObjectSize(item) + 8; // 8 bytes for reference
            }
        } else if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            size += 48; // Map overhead
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                size += estimateObjectSize(entry.getKey()) + estimateObjectSize(entry.getValue()) + 16;
            }
        }

        return size;
    }

    /**
     * Formats a memory size in human-readable format.
     *
     * @param bytes size in bytes
     * @return formatted size string
     */
    public static String formatMemorySize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * Checks if the current memory usage is safe for large operations.
     *
     * @param requiredMemoryMB required memory in MB
     * @return true if safe to proceed
     */
    public static boolean isMemoryAvailable(double requiredMemoryMB) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long availableMemory = maxMemory - usedMemory;

        long requiredBytes = (long) (requiredMemoryMB * 1024 * 1024);
        return availableMemory > requiredBytes * 2; // Keep 2x buffer
    }

    /**
     * Logs memory usage statistics.
     */
    public static void logMemoryStats() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        log.info("💾 Memory Stats:");
        log.info("   Max: " + formatMemorySize(maxMemory));
        log.info("   Used: " + formatMemorySize(usedMemory));
        log.info("   Free: " + formatMemorySize(freeMemory));
        log.info("   Available: " + formatMemorySize(maxMemory - usedMemory));
    }

    /**
     * Monitors database query performance.
     *
     * @param queryName the name of the query
     * @param query the query operation
     * @return the result of the query
     */
    public static <T> T monitorQuery(String queryName, ThrowingSupplier<T> query) {
        return monitor("DB Query: " + queryName, query);
    }

    /**
     * Monitors batch operation performance.
     *
     * @param operationName the name of the batch operation
     * @param batchSize the size of the batch
     * @param operation the batch operation
     * @return the result of the operation
     */
    public static <T> T monitorBatch(String operationName, int batchSize, ThrowingSupplier<T> operation) {
        return monitor(operationName + " (batch size: " + batchSize + ")", operation);
    }
}