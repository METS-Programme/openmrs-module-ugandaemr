package org.openmrs.module.ugandaemr.exception;

/**
 * Custom exception for UgandaEMR module specific errors.
 * Provides structured error handling with error codes and parameters for better error management and user messaging.
 */
public class UgandaEMRException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final Object[] errorParams;

    /**
     * Creates a new UgandaEMR exception with error code and message.
     *
     * @param errorCode the error code for categorization
     * @param message the error message
     */
    public UgandaEMRException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorParams = new Object[0];
    }

    /**
     * Creates a new UgandaEMR exception with error code, message, and cause.
     *
     * @param errorCode the error code for categorization
     * @param message the error message
     * @param cause the underlying cause
     */
    public UgandaEMRException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorParams = new Object[0];
    }

    /**
     * Creates a new UgandaEMR exception with error code, message, and parameters.
     *
     * @param errorCode the error code for categorization
     * @param message the error message
     * @param errorParams parameters for error message formatting
     */
    public UgandaEMRException(String errorCode, String message, Object... errorParams) {
        super(message);
        this.errorCode = errorCode;
        this.errorParams = errorParams != null ? errorParams : new Object[0];
    }

    /**
     * Creates a new UgandaEMR exception with all fields.
     *
     * @param errorCode the error code for categorization
     * @param message the error message
     * @param cause the underlying cause
     * @param errorParams parameters for error message formatting
     */
    public UgandaEMRException(String errorCode, String message, Throwable cause, Object... errorParams) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorParams = errorParams != null ? errorParams : new Object[0];
    }

    /**
     * Gets the error code for categorization and logging.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the error parameters for message formatting.
     *
     * @return the error parameters
     */
    public Object[] getErrorParams() {
        return errorParams;
    }

    /**
     * Gets a formatted error message with parameters.
     *
     * @return formatted error message
     */
    public String getFormattedMessage() {
        if (errorParams == null || errorParams.length == 0) {
            return getMessage();
        }

        try {
            return String.format(getMessage(), errorParams);
        } catch (Exception e) {
            return getMessage();
        }
    }

    @Override
    public String toString() {
        return String.format("UgandaEMRException[code=%s, message=%s]", errorCode, getMessage());
    }
}