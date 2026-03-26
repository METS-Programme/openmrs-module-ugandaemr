package org.openmrs.module.ugandaemr.api.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;
import org.openmrs.Provider;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "non_patient_queue")
public class NonPatientQueue extends BaseOpenmrsData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "non_patient_queue_id")
    private Integer nonPatientQueueId;

    @Column(name = "ticket_number", length = 50)
    private String ticketNumber;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "queue_type", length = 100)
    private NonPatientQueueType queueType;

    @Column(name = "status", length = 50)
    private NonPatientQueueStatus status;

    @ManyToOne
    @JoinColumn(name = "current_location")
    private Location currentLocation;

    @ManyToOne
    @JoinColumn(name = "location_to")
    private Location locationTo;

    @ManyToOne
    @JoinColumn(name = "queue_room")
    private Location queueRoom;

    @ManyToOne
    @JoinColumn(name = "called_by")
    private Provider calledBy;

    @ManyToOne
    @JoinColumn(name = "served_by")
    private Provider servedBy;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "called_at")
    private Date calledAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "arrived_at")
    private Date arrivedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "started_at")
    private Date startedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ended_at")
    private Date endedAt;

    public Integer getNonPatientQueueId() {
        return nonPatientQueueId;
    }

    public void setNonPatientQueueId(Integer nonPatientQueueId) {
        this.nonPatientQueueId = nonPatientQueueId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public NonPatientQueueType getQueueType() {
        return queueType;
    }

    public void setQueueType(NonPatientQueueType queueType) {
        this.queueType = queueType;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Location getLocationTo() {
        return locationTo;
    }

    public void setLocationTo(Location locationTo) {
        this.locationTo = locationTo;
    }

    public Location getQueueRoom() {
        return queueRoom;
    }

    public void setQueueRoom(Location queueRoom) {
        this.queueRoom = queueRoom;
    }

    public Provider getCalledBy() {
        return calledBy;
    }

    public void setCalledBy(Provider calledBy) {
        this.calledBy = calledBy;
    }

    public Provider getServedBy() {
        return servedBy;
    }

    public void setServedBy(Provider servedBy) {
        this.servedBy = servedBy;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(Date calledAt) {
        this.calledAt = calledAt;
    }

    public Date getArrivedAt() {
        return arrivedAt;
    }

    public void setArrivedAt(Date arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    @Override
    public Integer getId() {
        return nonPatientQueueId;
    }

    @Override
    public void setId(Integer id) {
        this.nonPatientQueueId = id;
    }

    public enum NonPatientQueueType {
        REGISTRATION,
        VISITOR,
        ADMIN,
        PAYMENT,
        RECORDS,
        OTHER;

        public static NonPatientQueueType fromString(String value) {

            if (value == null) {
                throw new IllegalArgumentException("queueType is required");
            }

            try {
                return NonPatientQueueType.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid queueType: " + value);
            }
        }
    }

    public enum NonPatientQueueStatus {
        WAITING,
        CALLED,
        ARRIVED,
        SERVING,
        COMPLETED,
        SKIPPED,
        CANCELLED
    }

    public NonPatientQueueStatus getStatus() {
        return status;
    }

    public void setStatus(NonPatientQueueStatus status) {
        this.status = status;
    }
}