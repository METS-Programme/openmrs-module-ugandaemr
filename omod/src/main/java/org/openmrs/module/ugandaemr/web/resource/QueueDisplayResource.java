package org.openmrs.module.ugandaemr.web.resource;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientqueueing.api.PatientQueueingService;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.ugandaemr.api.model.NonPatientQueue;
import org.openmrs.module.ugandaemr.web.customdto.QueueDisplayContextDto;
import org.openmrs.module.ugandaemr.web.customdto.QueueDisplayDto;
import org.openmrs.module.ugandaemr.web.customdto.QueueDisplayRowDto;
import org.openmrs.module.ugandaemr.web.customdto.QueueDisplayStatsDto;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.OpenmrsUtil;

import java.util.*;

/**
 * Patient-facing Queue Display feed (computed DTO). GET
 * /ws/rest/v1/patientqueueing/display?type=facility
 * |location|room&uuid=<locationUuid>&date=today|YYYY-MM-DD NOTE: Query-params are treated as SEARCH
 * by OpenMRS REST -> doSearch(...) must be implemented.
 */
@Resource(name = RestConstants.VERSION_1 + "/display", supportedClass = QueueDisplayDto.class, supportedOpenmrsVersions = {"1.8 - 9.0.*"})
public class QueueDisplayResource extends DelegatingCrudResource<QueueDisplayDto> {

    private PatientQueueingService service() {
        return Context.getService(PatientQueueingService.class);
    }

    @Override
    public QueueDisplayDto newDelegate() {
        return new QueueDisplayDto();
    }

    /**
     * This resource is computed/read-only - no persistence.
     */
    @Override
    public QueueDisplayDto save(QueueDisplayDto delegate) {
        throw new ResourceDoesNotSupportOperationException("QueueDisplayResource is read-only");
    }

    /**
     * Not supported: there is no persisted QueueDisplayDto by uuid. (We use doSearch with query
     * parameters.)
     */
    @Override
    public QueueDisplayDto getByUniqueId(String uniqueId) {
        throw new ResourceDoesNotSupportOperationException("QueueDisplayResource does not support getByUniqueId");
    }

    @Override
    protected void delete(QueueDisplayDto delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("QueueDisplayResource is read-only");
    }

    @Override
    public void purge(QueueDisplayDto delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("QueueDisplayResource is read-only");
    }

    /**
     * OpenMRS REST routes GET with query params here. Example: /display?type=facility&uuid=...
     */
    @Override
    protected PageableResult doSearch(RequestContext context) throws ResponseException {
        // We return a "single result" list so REST is happy with pageable results.
        // Clients can read results[0], but to keep payload shape identical to your DTO,
        // most clients call with v=custom and read the first entry.
        //
        // If you prefer returning the DTO object directly (not list), use BaseDelegatingResource instead.
        QueueDisplayDto dto = buildFromParams(context);
        List<QueueDisplayDto> one = Collections.singletonList(dto);
        return new org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging<QueueDisplayDto>(one, context);
    }

    /**
     * Optional: GET /display without params is not meaningful for a feed. We require uuid. We can
     * either throw or behave like doSearch.
     */
    @Override
    public PageableResult doGetAll(RequestContext context) throws ResponseException {
        // If no uuid, reject (prevents confusing "all displays for all facilities" behavior).
        String uuid = context.getParameter("uuid");
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new ResourceDoesNotSupportOperationException("uuid is required (use /display?type=...&uuid=...)");
        }
        QueueDisplayDto dto = buildFromParams(context);
        List<QueueDisplayDto> one = Collections.singletonList(dto);
        return new org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging<QueueDisplayDto>(one, context);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
            DelegatingResourceDescription d = new DelegatingResourceDescription();
            d.addProperty("context");
            d.addProperty("nowServing");
            d.addProperty("upNext");
            d.addProperty("stats");
            d.addProperty("lastUpdated");
            return d;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        throw new ResourceDoesNotSupportOperationException("QueueDisplayResource is read-only");
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        throw new ResourceDoesNotSupportOperationException("QueueDisplayResource is read-only");
    }

    // ----------------- Core builder -----------------

    private QueueDisplayDto buildFromParams(RequestContext context) throws ResponseException {
        String type = param(context, "type", "facility"); // facility|location|room
        String uuid = context.getParameter("uuid");
        String dateParam = param(context, "date", "today");

        if (uuid == null || uuid.trim().isEmpty()) {
            throw new UnsupportedOperationException("uuid is required");
        }

        Location loc = Context.getLocationService().getLocationByUuid(uuid);
        if (loc == null) {
            throw new UnsupportedOperationException("Location not found for uuid=" + uuid);
        }

        DateRange range = resolveDateRange(dateParam);

        List<PatientQueue> active;
        List<NonPatientQueue> activeNonPatient;
        if ("facility".equalsIgnoreCase(type)) {
            active = service().getPatientQueueByParentLocationFifo(loc, null, range.from, range.to, false);
            activeNonPatient = Context.getService(UgandaEMRService.class).getQueueEntriesByQueueRoom(loc, range.from, range.to);
            return buildDisplayPayload("FACILITY", loc, active, activeNonPatient);
        } else if ("location".equalsIgnoreCase(type)) {
            active = service().getPatientQueueListFifo(null, range.from, range.to, loc, null, null, null, null);
            activeNonPatient = Context.getService(UgandaEMRService.class).getQueueEntriesByQueueRoom(loc, range.from, range.to);
            return buildDisplayPayload("LOCATION", loc, active, activeNonPatient);
        } else if ("room".equalsIgnoreCase(type)) {
            active = service().getPatientQueueListFifo(null, range.from, range.to, null, null, null, null, loc);
            activeNonPatient = Context.getService(UgandaEMRService.class).getQueueEntriesByQueueRoom(loc, range.from, range.to);
            return buildDisplayPayload("ROOM", loc, active, activeNonPatient);
        } else {
            throw new UnsupportedOperationException("type must be facility|location|room");
        }
    }

    private QueueDisplayDto buildDisplayPayload(String type, Location contextLocation, List<PatientQueue> items, List<NonPatientQueue> nonPatientQueues) {
        List<QueueDisplayRowDto> nowServing = new ArrayList<QueueDisplayRowDto>();
        List<QueueDisplayRowDto> upNext = new ArrayList<QueueDisplayRowDto>();

        if (items != null) {
            for (PatientQueue pq : items) {
                PatientQueue.Status st = pq.getStatus();

                boolean isNow = st == PatientQueue.Status.PICKED;

                boolean isNext = st == PatientQueue.Status.PENDING;

                if (isNow)
                    nowServing.add(toRow(pq));
                else if (isNext)
                    upNext.add(toRow(pq));
            }
        }

        if (nonPatientQueues != null) {
            for (NonPatientQueue pq : nonPatientQueues) {
                NonPatientQueue.NonPatientQueueStatus st = pq.getStatus();

                boolean isNow = st == NonPatientQueue.NonPatientQueueStatus.CALLED;

                boolean isNext = st == NonPatientQueue.NonPatientQueueStatus.WAITING;

                if (isNow)
                    nowServing.add(toRowFromNonPatient(pq));
                else if (isNext)
                    upNext.add(toRowFromNonPatient(pq));
            }
        }

        Collections.sort(upNext, new Comparator<QueueDisplayRowDto>() {

            @Override
            public int compare(QueueDisplayRowDto a, QueueDisplayRowDto b) {
                int pa = a.getPriorityScore() != null ? a.getPriorityScore().intValue() : 0;
                int pb = b.getPriorityScore() != null ? b.getPriorityScore().intValue() : 0;
                int pcmp = Integer.compare(pb, pa);
                if (pcmp != 0)
                    return pcmp;

                Date da = a.getDateCreated();
                Date db = b.getDateCreated();
                if (da == null && db == null)
                    return 0;
                if (da == null)
                    return 1;
                if (db == null)
                    return -1;
                return da.compareTo(db);
            }
        });

        QueueDisplayDto dto = new QueueDisplayDto();
        dto.setContext(new QueueDisplayContextDto(type, contextLocation != null ? contextLocation.getUuid() : null,
                contextLocation != null ? contextLocation.getName() : null));
        dto.setNowServing(nowServing);
        dto.setUpNext(upNext);
        dto.setStats(new QueueDisplayStatsDto(nowServing.size(), upNext.size(), items != null ? items.size() : 0));
        dto.setLastUpdated(new Date());
        return dto;
    }

    private QueueDisplayRowDto toRow(PatientQueue pq) {
        QueueDisplayRowDto row = new QueueDisplayRowDto();
        row.setUuid(pq.getUuid());

        String ticket = pq.getVisitNumber();
        row.setTicketNumber(ticket);

        row.setStatus(pq.getStatus() != null ? pq.getStatus().name() : null);
        row.setQueueLocation(pq.getLocationTo() != null ? pq.getLocationTo().getName() : null);
        row.setServiceLocation(pq.getQueueRoom() != null ? pq.getQueueRoom().getName() : null);
        row.setDateCreated(pq.getDateCreated());
        row.setPriorityScore(pq.getPriorityScore());
        row.setPriorityReason(pq.getPriorityReason());
        return row;
    }

    private QueueDisplayRowDto toRowFromNonPatient(NonPatientQueue pq) {
        QueueDisplayRowDto row = new QueueDisplayRowDto();
        row.setUuid(pq.getUuid());

        String ticket = pq.getTicketNumber();
        row.setTicketNumber(ticket);

        row.setStatus(pq.getStatus() != null ? pq.getStatus().name() : null);
        row.setQueueLocation(pq.getLocationTo() != null ? pq.getLocationTo().getName() : null);
        row.setServiceLocation(pq.getQueueRoom() != null ? pq.getQueueRoom().getName() : null);
        row.setDateCreated(pq.getDateCreated());
        row.setPriorityScore(pq.getPriority());
        row.setPriorityReason(pq.getComment());
        return row;
    }

    private String param(RequestContext context, String name, String def) {
        String v = context.getParameter(name);
        return (v == null || v.trim().isEmpty()) ? def : v.trim();
    }

    private static class DateRange {

        private final Date from;

        private final Date to;

        private DateRange(Date from, Date to) {
            this.from = from;
            this.to = to;
        }
    }

    private DateRange resolveDateRange(String dateParam) {
        Date target = new Date();
        return new DateRange(OpenmrsUtil.firstSecondOfDay(target), OpenmrsUtil.getLastMomentOfDay(target));
    }
}
