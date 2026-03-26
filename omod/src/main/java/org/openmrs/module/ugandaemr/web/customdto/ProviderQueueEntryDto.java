package org.openmrs.module.ugandaemr.web.customdto;

import java.util.Date;

public class ProviderQueueEntryDto {

    private String uuid;
    private String queueNumber;
    private String displayName;
    private String identifiedBy;
    private String currentLocation;
    private String serviceLocation;
    private String status;
    private Integer priority;
    private String action;
    private Date dateCreated;

    /**
     * true  -> backed by PatientQueue
     * false -> backed by NonPatientQueue
     */
    private Boolean patient;

    /**
     * optional helper for updates / debugging
     * values: PATIENT_QUEUE or NON_PATIENT_QUEUE
     */
    private String sourceType;

    /**
     * used for updates when frontend sends the destination
     */
    private String locationToUuid;
    private String queueRoomUuid;
    private String comment;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(String queueNumber) {
        this.queueNumber = queueNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIdentifiedBy() {
        return identifiedBy;
    }

    public void setIdentifiedBy(String identifiedBy) {
        this.identifiedBy = identifiedBy;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(String serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getPatient() {
        return patient;
    }

    public void setPatient(Boolean patient) {
        this.patient = patient;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getLocationToUuid() {
        return locationToUuid;
    }

    public void setLocationToUuid(String locationToUuid) {
        this.locationToUuid = locationToUuid;
    }

    public String getQueueRoomUuid() {
        return queueRoomUuid;
    }

    public void setQueueRoomUuid(String queueRoomUuid) {
        this.queueRoomUuid = queueRoomUuid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}