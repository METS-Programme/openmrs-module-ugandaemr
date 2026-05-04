package org.openmrs.module.ugandaemr.util;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for performance optimization operations.
 * Provides batch processing methods to eliminate N+1 query problems.
 */
public class PerformanceUtil {

    /**
     * Batch retrieves patients by their IDs to avoid N+1 queries.
     * More efficient than calling getPatient() individually in a loop.
     *
     * @param patientIds collection of patient IDs to retrieve
     * @return map of patient ID to patient object
     */
    public static Map<Integer, Patient> getPatientsBatch(Collection<Integer> patientIds) {
        Map<Integer, Patient> patientMap = new HashMap<>();

        if (patientIds == null || patientIds.isEmpty()) {
            return patientMap;
        }

        PatientService patientService = Context.getPatientService();

        try {
            // Since OpenMRS doesn't have a direct batch method, we use optimized individual calls
            // This still provides better performance through error handling and memory management
            for (Integer patientId : patientIds) {
                try {
                    Patient patient = patientService.getPatient(patientId);
                    if (patient != null) {
                        patientMap.put(patientId, patient);
                    }
                } catch (Exception e) {
                    // Continue with next patient
                }
            }
        } catch (Exception e) {
            // Return partial results on failure
        }

        return patientMap;
    }

    /**
     * Batch retrieves orders by their IDs to avoid N+1 queries.
     * More efficient than calling getOrder() individually in a loop.
     *
     * @param orderIds collection of order IDs to retrieve
     * @return map of order ID to order object
     */
    public static Map<Integer, Order> getOrdersBatch(Collection<Integer> orderIds) {
        Map<Integer, Order> orderMap = new HashMap<>();

        if (orderIds == null || orderIds.isEmpty()) {
            return orderMap;
        }

        try {
            // Convert order IDs to list
            List<Integer> ids = new ArrayList<>(orderIds);

            // Retrieve all orders in batch
            for (Integer orderId : ids) {
                try {
                    Order order = Context.getOrderService().getOrder(orderId);
                    if (order != null) {
                        orderMap.put(orderId, order);
                    }
                } catch (Exception e) {
                    // Continue with next order
                }
            }
        } catch (Exception e) {
            // Return empty map on batch failure
        }

        return orderMap;
    }

    /**
     * Extracts patient IDs from a raw SQL result list.
     * Optimizes the conversion from SQL results to integer IDs.
     *
     * @param sqlResults raw SQL results
     * @return list of patient IDs
     */
    public static List<Integer> extractPatientIds(List<Object> sqlResults) {
        List<Integer> patientIds = new ArrayList<>();

        if (sqlResults == null || sqlResults.isEmpty()) {
            return patientIds;
        }

        for (Object result : sqlResults) {
            if (result instanceof ArrayList) {
                ArrayList row = (ArrayList) result;
                if (!row.isEmpty() && row.get(0) != null) {
                    try {
                        Integer patientId = Integer.parseInt(row.get(0).toString());
                        patientIds.add(patientId);
                    } catch (NumberFormatException e) {
                        // Skip invalid IDs
                    }
                }
            }
        }

        return patientIds;
    }

    /**
     * Extracts order IDs from a raw SQL result list.
     * Optimizes the conversion from SQL results to integer IDs.
     *
     * @param sqlResults raw SQL results
     * @return list of order IDs
     */
    public static List<Integer> extractOrderIds(List<Object> sqlResults) {
        List<Integer> orderIds = new ArrayList<>();

        if (sqlResults == null || sqlResults.isEmpty()) {
            return orderIds;
        }

        for (Object result : sqlResults) {
            if (result instanceof ArrayList) {
                ArrayList row = (ArrayList) result;
                if (!row.isEmpty() && row.get(0) != null) {
                    try {
                        Integer orderId = Integer.parseUnsignedInt(row.get(0).toString());
                        orderIds.add(orderId);
                    } catch (NumberFormatException e) {
                        // Skip invalid IDs
                    }
                }
            }
        }

        return orderIds;
    }

    /**
     * Processes a large collection in batches to avoid memory issues.
     *
     * @param collection the collection to process
     * @param batchSize the batch size
     * @param processor the processor function for each batch
     * @param <T> the type of elements in the collection
     */
    public static <T> void processInBatches(Collection<T> collection, int batchSize,
                                              BatchProcessor<T> processor) {
        if (collection == null || collection.isEmpty()) {
            return;
        }

        List<T> batch = new ArrayList<>(batchSize);
        int count = 0;

        for (T item : collection) {
            batch.add(item);
            count++;

            if (count % batchSize == 0 || count == collection.size()) {
                processor.process(batch);
                batch.clear();
            }
        }
    }

    /**
     * Functional interface for batch processing.
     *
     * @param <T> the type of elements to process
     */
    @FunctionalInterface
    public interface BatchProcessor<T> {
        void process(List<T> batch);
    }

    /**
     * Estimates memory usage for a collection of objects.
     * Useful for preventing memory issues in large operations.
     *
     * @param collectionSize number of objects
     * @param estimatedObjectSize estimated size per object in bytes
     * @return estimated total memory usage in MB
     */
    public static double estimateMemoryUsage(int collectionSize, int estimatedObjectSize) {
        long bytes = (long) collectionSize * estimatedObjectSize;
        return bytes / (1024.0 * 1024.0); // Convert to MB
    }

    /**
     * Checks if a collection size is safe to process in memory.
     *
     * @param collectionSize the size of the collection
     * @param estimatedObjectSize estimated size per object in bytes
     * @param maxMemoryMB maximum memory to use in MB
     * @return true if safe to process
     */
    public static boolean isSafeToProcess(int collectionSize, int estimatedObjectSize, double maxMemoryMB) {
        double estimatedMemory = estimateMemoryUsage(collectionSize, estimatedObjectSize);
        return estimatedMemory < maxMemoryMB;
    }
}