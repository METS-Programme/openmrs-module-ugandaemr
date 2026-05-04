package org.openmrs.module.ugandaemr.exception;

/**
 * Exception thrown when patient validation fails.
 */
public class PatientValidationException extends UgandaEMRException {

    private static final long serialVersionUID = 1L;

    public PatientValidationException(String message) {
        super("PATIENT_VALIDATION_ERROR", message);
    }

    public PatientValidationException(String message, Throwable cause) {
        super("PATIENT_VALIDATION_ERROR", message, cause);
    }

    public PatientValidationException(String message, Object... params) {
        super("PATIENT_VALIDATION_ERROR", message, params);
    }
}