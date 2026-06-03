package org.openmrs.module.ugandaemr.mapper;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.ugandaemr.api.queuemapper.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for mapping patient identifiers to UgandaEMR identifier objects.
 * Consolidates duplicate identifier mapping logic from multiple service methods.
 */
public class IdentifierMapperUtil {

    /**
     * Maps patient identifiers from a Patient object to UgandaEMR Identifier objects.
     *
     * @param patient the patient whose identifiers should be mapped
     * @return list of mapped identifiers, or empty list if patient has no identifiers
     */
    public static List<Identifier> mapPatientIdentifiers(Patient patient) {
        List<Identifier> identifiers = new ArrayList<>();

        if (patient == null || patient.getIdentifiers() == null) {
            return identifiers;
        }

        for (PatientIdentifier patientIdentifier : patient.getIdentifiers()) {
            if (patientIdentifier == null || patientIdentifier.getIdentifierType() == null) {
                continue;
            }

            Identifier identifier = new Identifier(
                patientIdentifier.getIdentifier(),
                patientIdentifier.getIdentifierType().getName(),
                patientIdentifier.getIdentifierType().getUuid()
            );

            identifiers.add(identifier);
        }

        return identifiers;
    }

    /**
     * Checks if a patient has a specific identifier type.
     *
     * @param patient the patient to check
     * @param identifierTypeUuid the UUID of the identifier type
     * @return true if the patient has an identifier of the specified type
     */
    public static boolean hasIdentifierType(Patient patient, String identifierTypeUuid) {
        if (patient == null || patient.getIdentifiers() == null || identifierTypeUuid == null) {
            return false;
        }

        for (PatientIdentifier patientIdentifier : patient.getIdentifiers()) {
            if (patientIdentifier != null &&
                patientIdentifier.getIdentifierType() != null &&
                identifierTypeUuid.equals(patientIdentifier.getIdentifierType().getUuid())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the first identifier of a specific type for a patient.
     *
     * @param patient the patient to get identifier from
     * @param identifierTypeUuid the UUID of the identifier type
     * @return the identifier value, or null if not found
     */
    public static String getIdentifierByType(Patient patient, String identifierTypeUuid) {
        if (patient == null || patient.getIdentifiers() == null || identifierTypeUuid == null) {
            return null;
        }

        for (PatientIdentifier patientIdentifier : patient.getIdentifiers()) {
            if (patientIdentifier != null &&
                patientIdentifier.getIdentifierType() != null &&
                identifierTypeUuid.equals(patientIdentifier.getIdentifierType().getUuid())) {
                return patientIdentifier.getIdentifier();
            }
        }

        return null;
    }
}