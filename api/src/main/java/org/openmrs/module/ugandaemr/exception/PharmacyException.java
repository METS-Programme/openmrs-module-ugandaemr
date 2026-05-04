package org.openmrs.module.ugandaemr.exception;

/**
 * Exception thrown when pharmacy operations fail.
 */
public class PharmacyException extends UgandaEMRException {

    private static final long serialVersionUID = 1L;

    public PharmacyException(String message) {
        super("PHARMACY_ERROR", message);
    }

    public PharmacyException(String message, Throwable cause) {
        super("PHARMACY_ERROR", message, cause);
    }

    public PharmacyException(String message, Object... params) {
        super("PHARMACY_ERROR", message, params);
    }
}