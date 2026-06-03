package org.openmrs.module.ugandaemr.exception;

/**
 * Exception thrown when laboratory operations fail.
 */
public class LaboratoryException extends UgandaEMRException {

    private static final long serialVersionUID = 1L;

    public LaboratoryException(String message) {
        super("LABORATORY_ERROR", message);
    }

    public LaboratoryException(String message, Throwable cause) {
        super("LABORATORY_ERROR", message, cause);
    }

    public LaboratoryException(String message, Object... params) {
        super("LABORATORY_ERROR", message, params);
    }
}