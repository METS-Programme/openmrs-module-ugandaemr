package org.openmrs.module.ugandaemr.web.customdto;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.module.ugandaemr.api.model.NonPatientQueue;

public class QueueKioskEntry {

    public enum QueueType {
        PATIENT,
        NON_PATIENT
    }

    private String uuid;
    private String ticketNumber;
    private String status;
    private String displayName;
    private QueueType queueType;
    private Location locationTo;
    private Location queueRoom;
    private Date datePicked;
    private Date dateCompleted;
    private Date dateCancelled;
    private Date dateCreated;
    private Date dateChanged;
    private Patient patient;

    private PatientQueue patientQueue;
    private NonPatientQueue nonPatientQueue;

    public QueueKioskEntry() {
    }

    public QueueKioskEntry(PatientQueue patientQueue) {
        this.patientQueue = patientQueue;
        this.queueType = QueueType.PATIENT;

        if (patientQueue != null) {
            this.uuid = patientQueue.getUuid();
            this.ticketNumber = patientQueue.getVisitNumber();
            this.status = patientQueue.getStatus() != null ? patientQueue.getStatus().name() : null;
            this.locationTo = patientQueue.getLocationTo();
            this.queueRoom = patientQueue.getQueueRoom();
            this.datePicked = patientQueue.getDatePicked();
            this.dateCompleted = patientQueue.getDateCompleted();
            this.dateCancelled = patientQueue.getDateCancelled();
            this.dateCreated = patientQueue.getDateCreated();
            this.dateChanged = patientQueue.getDateChanged();
            this.patient = patientQueue.getPatient();
            this.displayName = patientQueue.getPatient() != null && patientQueue.getPatient().getPersonName() != null
                    ? patientQueue.getPatient().getPersonName().getFullName()
                    : null;
        }
    }

    public QueueKioskEntry(NonPatientQueue nonPatientQueue) {
        this.nonPatientQueue = nonPatientQueue;
        this.queueType = QueueType.NON_PATIENT;

        if (nonPatientQueue != null) {
            this.uuid = nonPatientQueue.getUuid();
            this.ticketNumber = nonPatientQueue.getTicketNumber();
            this.status = nonPatientQueue.getStatus() != null ? nonPatientQueue.getStatus().name() : null;
            this.queueRoom = nonPatientQueue.getQueueRoom();
            this.dateCreated = nonPatientQueue.getDateCreated();
            this.displayName = nonPatientQueue.getDisplayName();
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getDisplayName() {
        return displayName;
    }

    public QueueType getQueueType() {
        return queueType;
    }

    public Location getLocationTo() {
        return locationTo;
    }

    public Location getQueueRoom() {
        return queueRoom;
    }

    public Date getDatePicked() {
        return datePicked;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public Date getDateCancelled() {
        return dateCancelled;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getDateChanged() {
        return dateChanged;
    }

    public Patient getPatient() {
        return patient;
    }

    public PatientQueue getPatientQueue() {
        return patientQueue;
    }

    public NonPatientQueue getNonPatientQueue() {
        return nonPatientQueue;
    }
}