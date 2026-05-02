package org.openmrs.module.ugandaemr.activator;

/**
 * Exception thrown when a critical module initialization step fails.
 * This exception indicates that the module cannot start successfully
 * and should be stopped.
 */
public class ModuleInitializationException extends RuntimeException {

    /**
     * Constructs a new module initialization exception with the specified detail message.
     *
     * @param message the detail message explaining what initialization failed
     */
    public ModuleInitializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new module initialization exception with the specified detail message and cause.
     *
     * @param message the detail message explaining what initialization failed
     * @param cause   the cause of the initialization failure
     */
    public ModuleInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}