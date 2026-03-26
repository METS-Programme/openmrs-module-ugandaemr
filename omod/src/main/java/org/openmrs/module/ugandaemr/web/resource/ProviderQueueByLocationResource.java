package org.openmrs.module.ugandaemr.web.resource;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientqueueing.api.PatientQueueingService;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.ugandaemr.api.model.NonPatientQueue;
import org.openmrs.module.ugandaemr.web.customdto.ProviderQueueEntryDto;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Resource(
        name = RestConstants.VERSION_1 + "/providerqueuebylocation",
        supportedClass = ProviderQueueEntryDto.class,
        supportedOpenmrsVersions = { "1.8 - 9.0.*" }
)
public class ProviderQueueByLocationResource extends DelegatingCrudResource<ProviderQueueEntryDto> {

    private PatientQueueingService patientQueueingService() {
        return Context.getService(PatientQueueingService.class);
    }

    private UgandaEMRService ugandaEMRService() {
        return Context.getService(UgandaEMRService.class);
    }

    private LocationService locationService() {
        return Context.getLocationService();
    }

    @Override
    public ProviderQueueEntryDto newDelegate() {
        return new ProviderQueueEntryDto();
    }

    @Override
    public ProviderQueueEntryDto save(ProviderQueueEntryDto delegate) {
        if (delegate.getUuid() == null || delegate.getUuid().trim().isEmpty()) {
            throw new IllegalArgumentException("uuid is required");
        }

        Boolean isPatient = delegate.getPatient();
        if (isPatient == null && delegate.getSourceType() != null) {
            isPatient = "PATIENT_QUEUE".equalsIgnoreCase(delegate.getSourceType());
        }

        if ("FORWARD".equalsIgnoreCase(delegate.getAction())) {
            if (Boolean.TRUE.equals(isPatient)) {
                return forwardPatientQueue(delegate);
            } else {
                return forwardNonPatientQueue(delegate);
            }
        }

        if (Boolean.TRUE.equals(isPatient)) {
            return updatePatientQueue(delegate);
        } else {
            return updateNonPatientQueue(delegate);
        }
    }

    @Override
    public ProviderQueueEntryDto getByUniqueId(String uniqueId) {
        PatientQueue patientQueue = patientQueueingService().getPatientQueueByUuid(uniqueId);
        if (patientQueue != null) {
            return toDto(patientQueue);
        }

        NonPatientQueue nonPatientQueue = ugandaEMRService().getQueueEntryByUuid(uniqueId);
        if (nonPatientQueue != null) {
            return toDto(nonPatientQueue);
        }

        return null;
    }

    @Override
    protected void delete(ProviderQueueEntryDto delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Delete is not supported");
    }

    @Override
    public void purge(ProviderQueueEntryDto delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Purge is not supported");
    }

    @Override
    public PageableResult doGetAll(RequestContext context) throws ResponseException {
        String uuid = context.getParameter("uuid");
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("uuid is required");
        }

        Location queueRoom = locationService().getLocationByUuid(uuid);
        if (queueRoom == null) {
            throw new IllegalArgumentException("Location not found for uuid=" + uuid);
        }

        Date target = new Date();
        Date from = OpenmrsUtil.firstSecondOfDay(target);
        Date to = OpenmrsUtil.getLastMomentOfDay(target);

        List<ProviderQueueEntryDto> rows = new ArrayList<ProviderQueueEntryDto>();

        List<PatientQueue> patientQueues = patientQueueingService().getPatientQueueListFifo(
                null,
                from,
                to,
                null,
                null,
                null,
                null,
                queueRoom
        );

        if (patientQueues != null) {
            for (PatientQueue pq : patientQueues) {
                rows.add(toDto(pq));
            }
        }

        List<NonPatientQueue> nonPatientQueues = ugandaEMRService().getQueueEntriesByQueueRoom(
                queueRoom,
                from,
                to
        );

        if (nonPatientQueues != null) {
            for (NonPatientQueue npq : nonPatientQueues) {
                rows.add(toDto(npq));
            }
        }

        Collections.sort(rows, new Comparator<ProviderQueueEntryDto>() {
            @Override
            public int compare(ProviderQueueEntryDto a, ProviderQueueEntryDto b) {
                int sa = statusRank(a.getStatus());
                int sb = statusRank(b.getStatus());

                int statusCompare = Integer.compare(sa, sb);
                if (statusCompare != 0) {
                    return statusCompare;
                }

                Integer pa = a.getPriority() != null ? a.getPriority() : 0;
                Integer pb = b.getPriority() != null ? b.getPriority() : 0;

                int priorityCompare = Integer.compare(pb, pa);
                if (priorityCompare != 0) {
                    return priorityCompare;
                }

                Date da = a.getDateCreated();
                Date db = b.getDateCreated();

                if (da == null && db == null) return 0;
                if (da == null) return 1;
                if (db == null) return -1;

                return da.compareTo(db);
            }
        });

        return new AlreadyPaged<ProviderQueueEntryDto>(context, rows, false);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) throws ResponseException {
        return doGetAll(context);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("queueNumber");
            description.addProperty("displayName");
            description.addProperty("identifiedBy");
            description.addProperty("currentLocation");
            description.addProperty("serviceLocation");
            description.addProperty("status");
            description.addProperty("priority");
            description.addProperty("dateCreated");
            description.addProperty("patient");
            description.addProperty("sourceType");
            description.addProperty("locationToUuid");
            description.addProperty("queueRoomUuid");
            description.addProperty("comment");
            description.addProperty("action");
            return description;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        throw new ResourceDoesNotSupportOperationException("Create is not supported here");
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("uuid");
        description.addProperty("patient");
        description.addProperty("sourceType");
        description.addProperty("status");
        description.addProperty("priority");
        description.addProperty("locationToUuid");
        description.addProperty("queueRoomUuid");
        description.addProperty("comment");
        description.addProperty("action");
        return description;
    }

    private ProviderQueueEntryDto updatePatientQueue(ProviderQueueEntryDto dto) {
        PatientQueue pq = patientQueueingService().getPatientQueueByUuid(dto.getUuid());
        if (pq == null) {
            throw new IllegalArgumentException("PatientQueue not found for uuid=" + dto.getUuid());
        }

        if (dto.getStatus() != null) {
            pq.setStatus(toPatientQueueStatus(dto.getStatus()));
            if (PatientQueue.Status.PICKED.equals(pq.getStatus())) {
                pq.setDatePicked(new Date());
            }
            if (PatientQueue.Status.COMPLETED.equals(pq.getStatus())) {
                pq.setDateCompleted(new Date());
            }
        }

        if (dto.getPriority() != null) {
            pq.setPriority(dto.getPriority());
        }

        if (dto.getLocationToUuid() != null && !dto.getLocationToUuid().trim().isEmpty()) {
            Location locationTo = locationService().getLocationByUuid(dto.getLocationToUuid());
            if (locationTo == null) {
                throw new IllegalArgumentException("locationTo not found for uuid=" + dto.getLocationToUuid());
            }
            pq.setLocationTo(locationTo);
        }

        if (dto.getQueueRoomUuid() != null && !dto.getQueueRoomUuid().trim().isEmpty()) {
            Location queueRoom = locationService().getLocationByUuid(dto.getQueueRoomUuid());
            if (queueRoom == null) {
                throw new IllegalArgumentException("queueRoom not found for uuid=" + dto.getQueueRoomUuid());
            }
            pq.setQueueRoom(queueRoom);
        }

        if (dto.getComment() != null) {
            pq.setComment(dto.getComment());
        }

        patientQueueingService().savePatientQue(pq);
        return toDto(pq);
    }

    private ProviderQueueEntryDto updateNonPatientQueue(ProviderQueueEntryDto dto) {
        NonPatientQueue npq = ugandaEMRService().getQueueEntryByUuid(dto.getUuid());
        if (npq == null) {
            throw new IllegalArgumentException("NonPatientQueue not found for uuid=" + dto.getUuid());
        }

        if (dto.getStatus() != null) {
            npq.setStatus(toNonPatientQueueStatus(dto.getStatus()));
            if (NonPatientQueue.NonPatientQueueStatus.CALLED.equals(npq.getStatus())) {
                npq.setCalledAt(new Date());
            }
            if (NonPatientQueue.NonPatientQueueStatus.COMPLETED.equals(npq.getStatus())) {
                npq.setEndedAt(new Date());
            }
        }

        if (dto.getPriority() != null) {
            npq.setPriority(dto.getPriority());
        }

        if (dto.getLocationToUuid() != null && !dto.getLocationToUuid().trim().isEmpty()) {
            Location locationTo = locationService().getLocationByUuid(dto.getLocationToUuid());
            if (locationTo == null) {
                throw new IllegalArgumentException("locationTo not found for uuid=" + dto.getLocationToUuid());
            }
            npq.setLocationTo(locationTo);
        }

        if (dto.getQueueRoomUuid() != null && !dto.getQueueRoomUuid().trim().isEmpty()) {
            Location queueRoom = locationService().getLocationByUuid(dto.getQueueRoomUuid());
            if (queueRoom == null) {
                throw new IllegalArgumentException("queueRoom not found for uuid=" + dto.getQueueRoomUuid());
            }
            npq.setQueueRoom(queueRoom);
        }

        if (dto.getComment() != null) {
            npq.setComment(dto.getComment());
        }

        ugandaEMRService().saveQueueEntry(npq);
        return toDto(npq);
    }

    private ProviderQueueEntryDto forwardPatientQueue(ProviderQueueEntryDto dto) {
        PatientQueue current = patientQueueingService().getPatientQueueByUuid(dto.getUuid());
        if (current == null) {
            throw new IllegalArgumentException("PatientQueue not found for uuid=" + dto.getUuid());
        }

        Location nextLocation = requireLocation(dto.getLocationToUuid(), "locationToUuid");
        Location nextQueueRoom = requireLocation(dto.getQueueRoomUuid(), "queueRoomUuid");

        current.setStatus(PatientQueue.Status.COMPLETED);
        current.setDateCompleted(new Date());
        if (dto.getComment() != null) {
            current.setComment(dto.getComment());
        }
        patientQueueingService().savePatientQue(current);

        PatientQueue next = new PatientQueue();
        next.setPatient(current.getPatient());
        next.setProvider(current.getProvider());
        next.setLocationFrom(current.getLocationTo() != null ? current.getLocationTo() : current.getQueueRoom());
        next.setLocationTo(nextLocation);
        next.setEncounter(current.getEncounter());
        next.setStatus(PatientQueue.Status.PENDING);
        next.setVisitNumber(current.getVisitNumber());
        next.setPriority(current.getPriority());
        next.setPriorityComment(current.getPriorityComment());
        next.setComment(dto.getComment() != null ? dto.getComment() : current.getComment());
        next.setQueueRoom(nextQueueRoom);
        next.setDatePicked(null);
        next.setDateCompleted(null);

        patientQueueingService().savePatientQue(next);
        return toDto(next);
    }

    private ProviderQueueEntryDto forwardNonPatientQueue(ProviderQueueEntryDto dto) {
        NonPatientQueue current = ugandaEMRService().getQueueEntryByUuid(dto.getUuid());
        if (current == null) {
            throw new IllegalArgumentException("NonPatientQueue not found for uuid=" + dto.getUuid());
        }

        Location nextLocation = requireLocation(dto.getLocationToUuid(), "locationToUuid");
        Location nextQueueRoom = requireLocation(dto.getQueueRoomUuid(), "queueRoomUuid");

        current.setStatus(NonPatientQueue.NonPatientQueueStatus.COMPLETED);
        current.setEndedAt(new Date());
        if (dto.getComment() != null) {
            current.setComment(dto.getComment());
        }
        ugandaEMRService().saveQueueEntry(current);

        NonPatientQueue next = new NonPatientQueue();
        next.setTicketNumber(current.getTicketNumber());
        next.setDisplayName(current.getDisplayName());
        next.setPhoneNumber(current.getPhoneNumber());
        next.setQueueType(current.getQueueType());
        next.setStatus(NonPatientQueue.NonPatientQueueStatus.WAITING);
        next.setCurrentLocation(current.getLocationTo() != null ? current.getLocationTo() : current.getQueueRoom());
        next.setLocationTo(nextLocation);
        next.setQueueRoom(nextQueueRoom);
        next.setPriority(current.getPriority());
        next.setComment(dto.getComment() != null ? dto.getComment() : current.getComment());
        next.setCalledBy(null);
        next.setServedBy(null);
        next.setCalledAt(null);
        next.setArrivedAt(null);
        next.setStartedAt(null);
        next.setEndedAt(null);

        ugandaEMRService().saveQueueEntry(next);
        return toDto(next);
    }

    private Location requireLocation(String uuid, String fieldName) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        Location location = locationService().getLocationByUuid(uuid);
        if (location == null) {
            throw new IllegalArgumentException("Location not found for uuid=" + uuid);
        }

        return location;
    }

    private ProviderQueueEntryDto toDto(PatientQueue pq) {
        ProviderQueueEntryDto dto = new ProviderQueueEntryDto();
        dto.setUuid(pq.getUuid());
        dto.setQueueNumber(pq.getVisitNumber());
        dto.setDisplayName(resolvePatientName(pq));
        dto.setIdentifiedBy(resolvePatientIdentifierLabel(pq));
        dto.setCurrentLocation(pq.getLocationTo() != null ? pq.getLocationTo().getName() : null);
        dto.setServiceLocation(pq.getQueueRoom() != null ? pq.getQueueRoom().getName() : null);
        dto.setStatus(mapPatientStatus(pq.getStatus()));
        dto.setPriority(pq.getPriority());
        dto.setDateCreated(pq.getDateCreated());
        dto.setPatient(true);
        dto.setSourceType("PATIENT_QUEUE");
        dto.setLocationToUuid(pq.getLocationTo() != null ? pq.getLocationTo().getUuid() : null);
        dto.setQueueRoomUuid(pq.getQueueRoom() != null ? pq.getQueueRoom().getUuid() : null);
        dto.setComment(pq.getComment());
        return dto;
    }

    private ProviderQueueEntryDto toDto(NonPatientQueue npq) {
        ProviderQueueEntryDto dto = new ProviderQueueEntryDto();
        dto.setUuid(npq.getUuid());
        dto.setQueueNumber(npq.getTicketNumber());
        dto.setDisplayName(npq.getDisplayName());
        dto.setIdentifiedBy("Other");
        dto.setCurrentLocation(npq.getLocationTo() != null ? npq.getLocationTo().getName() : null);
        dto.setServiceLocation(npq.getQueueRoom() != null ? npq.getQueueRoom().getName() : null);
        dto.setStatus(mapNonPatientStatus(npq.getStatus()));
        dto.setPriority(npq.getPriority());
        dto.setDateCreated(npq.getDateCreated());
        dto.setPatient(false);
        dto.setSourceType("NON_PATIENT_QUEUE");
        dto.setLocationToUuid(npq.getLocationTo() != null ? npq.getLocationTo().getUuid() : null);
        dto.setQueueRoomUuid(npq.getQueueRoom() != null ? npq.getQueueRoom().getUuid() : null);
        dto.setComment(npq.getComment());
        return dto;
    }

    private String resolvePatientName(PatientQueue pq) {
        if (pq.getPatient() != null && pq.getPatient().getPersonName() != null) {
            return pq.getPatient().getPersonName().getFullName();
        }
        return "Patient";
    }

    private String resolvePatientIdentifierLabel(PatientQueue pq) {
        if (pq.getPatient() != null
                && pq.getPatient().getActiveIdentifiers() != null
                && !pq.getPatient().getActiveIdentifiers().isEmpty()
                && pq.getPatient().getActiveIdentifiers().iterator().next().getIdentifierType() != null) {
            return pq.getPatient().getActiveIdentifiers().iterator().next().getIdentifierType().getName();
        }
        return "Patient Record";
    }

    private String mapPatientStatus(PatientQueue.Status status) {
        if (status == null) return "WAITING";
        if (status == PatientQueue.Status.PENDING) return "WAITING";
        if (status == PatientQueue.Status.PICKED) return "CALLED";
        return status.name();
    }

    private String mapNonPatientStatus(NonPatientQueue.NonPatientQueueStatus status) {
        if (status == null) return "WAITING";
        if (status == NonPatientQueue.NonPatientQueueStatus.WAITING) return "WAITING";
        if (status == NonPatientQueue.NonPatientQueueStatus.CALLED) return "CALLED";
        if (status == NonPatientQueue.NonPatientQueueStatus.SERVING) return "IN_SERVICE";
        return status.name();
    }

    private PatientQueue.Status toPatientQueueStatus(String status) {
        String s = status != null ? status.trim().toUpperCase() : "WAITING";

        if ("WAITING".equals(s)) return PatientQueue.Status.PENDING;
        if ("CALLED".equals(s)) return PatientQueue.Status.PICKED;
        if ("IN_SERVICE".equals(s)) return PatientQueue.Status.PICKED;

        try {
            return PatientQueue.Status.valueOf(s);
        } catch (Exception e) {
            return PatientQueue.Status.PENDING;
        }
    }

    private NonPatientQueue.NonPatientQueueStatus toNonPatientQueueStatus(String status) {
        String s = status != null ? status.trim().toUpperCase() : "WAITING";

        if ("IN_SERVICE".equals(s)) {
            return NonPatientQueue.NonPatientQueueStatus.SERVING;
        }

        try {
            return NonPatientQueue.NonPatientQueueStatus.valueOf(s);
        } catch (Exception e) {
            return NonPatientQueue.NonPatientQueueStatus.WAITING;
        }
    }

    private int statusRank(String status) {
        String s = status != null ? status.toUpperCase() : "";

        if ("IN_SERVICE".equals(s)) return 0;
        if ("CALLED".equals(s)) return 1;
        if ("WAITING".equals(s)) return 2;
        if ("SKIPPED".equals(s)) return 3;
        if ("COMPLETED".equals(s)) return 4;

        return 5;
    }
}