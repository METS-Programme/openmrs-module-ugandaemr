package org.openmrs.module.ugandaemr.cache;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.util.ValidationUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple cache manager for frequently accessed metadata.
 * Improves performance by caching expensive database lookups.
 */
public class MetadataCache {

    private static final long CACHE_EXPIRY_MS = 5 * 60 * 1000; // 5 minutes

    // Simple in-memory cache with expiry times
    private static final Map<String, CacheEntry> conceptCache = new ConcurrentHashMap<>();
    private static final Map<String, CacheEntry> encounterTypeCache = new ConcurrentHashMap<>();

    /**
     * Gets a concept by UUID, using cache if available.
     *
     * @param uuid the concept UUID
     * @return the concept, or null if not found
     */
    public static Concept getConcept(String uuid) {
        if (uuid == null || !ValidationUtil.isValidUUID(uuid)) {
            return null;
        }

        // Check cache
        CacheEntry cached = conceptCache.get(uuid);
        if (cached != null && !cached.isExpired()) {
            return (Concept) cached.getValue();
        }

        // Load from service
        try {
            ConceptService conceptService = Context.getConceptService();
            Concept concept = conceptService.getConceptByUuid(uuid);
            if (concept != null) {
                conceptCache.put(uuid, new CacheEntry(concept));
            }
            return concept;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets a concept by ID, using cache if available.
     *
     * @param conceptId the concept ID
     * @return the concept, or null if not found
     */
    public static Concept getConcept(Integer conceptId) {
        if (conceptId == null) {
            return null;
        }

        String cacheKey = "ID:" + conceptId;

        // Check cache
        CacheEntry cached = conceptCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return (Concept) cached.getValue();
        }

        // Load from service
        try {
            ConceptService conceptService = Context.getConceptService();
            Concept concept = conceptService.getConcept(conceptId);
            if (concept != null) {
                conceptCache.put(cacheKey, new CacheEntry(concept));
            }
            return concept;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets an encounter type by UUID, using cache if available.
     *
     * @param uuid the encounter type UUID
     * @return the encounter type, or null if not found
     */
    public static EncounterType getEncounterType(String uuid) {
        if (uuid == null || !ValidationUtil.isValidUUID(uuid)) {
            return null;
        }

        // Check cache
        CacheEntry cached = encounterTypeCache.get(uuid);
        if (cached != null && !cached.isExpired()) {
            return (EncounterType) cached.getValue();
        }

        // Load from service
        try {
            EncounterService encounterService = Context.getEncounterService();
            EncounterType encounterType = encounterService.getEncounterTypeByUuid(uuid);
            if (encounterType != null) {
                encounterTypeCache.put(uuid, new CacheEntry(encounterType));
            }
            return encounterType;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Clears all caches.
     * Should be called when metadata is updated.
     */
    public static void clearAll() {
        conceptCache.clear();
        encounterTypeCache.clear();
    }

    /**
     * Clears expired entries from all caches.
     */
    public static void cleanExpired() {
        cleanExpiredEntries(conceptCache);
        cleanExpiredEntries(encounterTypeCache);
    }

    /**
     * Removes expired entries from a cache.
     */
    private static void cleanExpiredEntries(Map<String, CacheEntry> cache) {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * Gets cache statistics for monitoring.
     *
     * @return map of cache names to their sizes
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("concepts", conceptCache.size());
        stats.put("encounterTypes", encounterTypeCache.size());
        return stats;
    }

    /**
     * Internal cache entry with expiry time.
     */
    private static class CacheEntry {
        private final Object value;
        private final long expiryTime;

        public CacheEntry(Object value) {
            this.value = value;
            this.expiryTime = System.currentTimeMillis() + CACHE_EXPIRY_MS;
        }

        public Object getValue() {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}