package org.openmrs.module.ugandaemr.api.queuemapper;

import org.openmrs.Visit;
import org.openmrs.module.patientqueueing.model.PatientQueue;

import java.io.Serializable;

public class CheckInPatient implements Serializable {

    String uuid;
    private PatientQueue patientQueue;

    private Visit visit;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public PatientQueue getPatientQueue() {
        return patientQueue;
    }

    public void setPatientQueue(PatientQueue patientQueue) {
        this.patientQueue = patientQueue;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }
}
