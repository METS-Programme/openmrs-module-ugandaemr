package org.openmrs.module.ugandaemr.util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility class for algorithmic optimizations.
 * Provides efficient alternatives to common operations that may have performance issues.
 */
public class AlgorithmUtil {

    /**
     * Efficiently filters a collection using a predicate.
     * More efficient than manual iteration with if statements.
     *
     * @param collection the collection to filter
     * @param predicate the filtering condition
     * @return list of filtered elements
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        return collection.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Efficiently transforms a collection using a function.
     * More efficient than manual iteration and object creation.
     *
     * @param collection the collection to transform
     * @param mapper the transformation function
     * @return list of transformed elements
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> mapper) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        return collection.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * Efficiently groups elements by a key function.
     * More efficient than manual map construction.
     *
     * @param collection the collection to group
     * @param classifier the grouping function
     * @return map of groups
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> collection, Function<T, K> classifier) {
        if (collection == null || collection.isEmpty()) {
            return new HashMap<>();
        }

        return collection.stream()
                .collect(Collectors.groupingBy(classifier));
    }

    /**
     * Creates a map from a collection using a key extractor.
     * More efficient than manual map construction with null checks.
     *
     * @param collection the collection to convert
     * @param keyExtractor the function to extract keys
     * @return map of elements by their keys
     */
    public static <T, K> Map<K, T> toMap(Collection<T> collection, Function<T, K> keyExtractor) {
        if (collection == null || collection.isEmpty()) {
            return new HashMap<>();
        }

        return collection.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                    keyExtractor,
                    Function.identity(),
                    (existing, replacement) -> existing // Keep first occurrence on duplicates
                ));
    }

    /**
     * Removes duplicates from a collection while preserving order.
     * More efficient than manual duplicate checking.
     *
     * @param collection the collection to deduplicate
     * @return list without duplicates
     */
    public static <T> List<T> distinct(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        return collection.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Finds the first element matching a predicate.
     * More efficient than iterating through entire collection.
     *
     * @param collection the collection to search
     * @param predicate the search condition
     * @return first matching element, or null if not found
     */
    public static <T> T findFirst(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }

        return collection.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if any element matches a predicate.
     * More efficient than iterating through entire collection.
     *
     * @param collection the collection to check
     * @param predicate the condition to check
     * @return true if any element matches
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }

        return collection.stream()
                .anyMatch(predicate);
    }

    /**
     * Efficiently joins a collection of strings with a delimiter.
     * More efficient than manual string concatenation.
     *
     * @param strings the strings to join
     * @param delimiter the delimiter
     * @return joined string
     */
    public static String join(Collection<String> strings, String delimiter) {
        if (strings == null || strings.isEmpty()) {
            return "";
        }

        return String.join(delimiter, strings);
    }

    /**
     * Partitions a collection into batches of specified size.
     * Useful for batch processing to avoid memory issues.
     *
     * @param collection the collection to partition
     * @param batchSize the batch size
     * @return list of batches
     */
    public static <T> List<List<T>> partition(Collection<T> collection, int batchSize) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        List<List<T>> batches = new ArrayList<>();
        List<T> currentBatch = new ArrayList<>(batchSize);
        int count = 0;

        for (T item : collection) {
            currentBatch.add(item);
            count++;

            if (count % batchSize == 0 || count == collection.size()) {
                batches.add(new ArrayList<>(currentBatch));
                currentBatch.clear();
            }
        }

        return batches;
    }

    /**
     * Counts elements matching a predicate efficiently.
     * More efficient than manual counter incrementation.
     *
     * @param collection the collection to count
     * @param predicate the condition to count
     * @return count of matching elements
     */
    public static <T> long count(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || collection.isEmpty()) {
            return 0;
        }

        return collection.stream()
                .filter(predicate)
                .count();
    }

    /**
     * Efficiently sorts a collection with a comparator.
     * More efficient than manual sorting algorithms.
     *
     * @param collection the collection to sort
     * @param comparator the comparison function
     * @return sorted list
     */
    public static <T> List<T> sort(Collection<T> collection, Comparator<? super T> comparator) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> list = new ArrayList<>(collection);
        list.sort(comparator);
        return list;
    }

    /**
     * Limits a collection to a maximum size efficiently.
     * Useful for preventing excessive memory usage.
     *
     * @param collection the collection to limit
     * @param maxSize the maximum size
     * @return limited list
     */
    public static <T> List<T> limit(Collection<T> collection, int maxSize) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        return collection.stream()
                .limit(maxSize)
                .collect(Collectors.toList());
    }

    /**
     * Skips the first n elements of a collection efficiently.
     * Useful for pagination and offset operations.
     *
     * @param collection the collection
     * @param n number of elements to skip
     * @return remaining elements as list
     */
    public static <T> List<T> skip(Collection<T> collection, int n) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        return collection.stream()
                .skip(n)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a collection is null or empty efficiently.
     *
     * @param collection the collection to check
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks if a collection has elements efficiently.
     *
     * @param collection the collection to check
     * @return true if has elements
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Creates an empty collection if the input is null.
     * Prevents null pointer exceptions.
     *
     * @param collection the collection to check
     * @return the collection or empty collection if null
     */
    public static <T extends Collection<?>> T emptyIfNull(T collection) {
        return collection == null ? (T) Collections.emptyList() : collection;
    }

    /**
     * Sums integer values in a collection efficiently.
     *
     * @param collection the collection of integers
     * @return sum of values
     */
    public static int sumInt(Collection<Integer> collection) {
        if (collection == null || collection.isEmpty()) {
            return 0;
        }

        return collection.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Averages integer values in a collection efficiently.
     *
     * @param collection the collection of integers
     * @return average of values, or 0 if empty
     */
    public static double averageInt(Collection<Integer> collection) {
        if (collection == null || collection.isEmpty()) {
            return 0.0;
        }

        return collection.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Finds maximum value in a collection efficiently.
     *
     * @param collection the collection of comparable elements
     * @return maximum value, or null if empty
     */
    public static <T extends Comparable<? super T>> T max(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }

        return collection.stream()
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    /**
     * Finds minimum value in a collection efficiently.
     *
     * @param collection the collection of comparable elements
     * @return minimum value, or null if empty
     */
    public static <T extends Comparable<? super T>> T min(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }

        return collection.stream()
                .min(Comparator.naturalOrder())
                .orElse(null);
    }
}