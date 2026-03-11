package org.openmrs.module.ugandaemr.web.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientqueueing.api.PatientQueueingService;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.ugandaemr.web.customdto.QueueKioskEntry;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.OpenmrsUtil;

@Resource(
        name = RestConstants.VERSION_1 + "/kiosk",
        supportedClass = QueueKioskEntry.class,
        supportedOpenmrsVersions = { "1.8 - 9.0.*" })
public class QueueKioskResource extends DelegatingCrudResource<QueueKioskEntry> {

    private PatientQueueingService patientQueueService() {
        return Context.getService(PatientQueueingService.class);
    }

    private UgandaEMRService ugandaEMRService() {
        return Context.getService(UgandaEMRService.class);
    }

    @Override
    public QueueKioskEntry newDelegate() {
        return new QueueKioskEntry();
    }

    @Override
    public QueueKioskEntry getByUniqueId(String uniqueId) {
        throw new ResourceDoesNotSupportOperationException("QueueKioskResource does not support getByUniqueId");
    }

    @Override
    protected NeedsPaging<QueueKioskEntry> doSearch(RequestContext context) throws ResponseException {
        String queueNumber = context.getParameter("ticketNumber");
        if (isBlank(queueNumber)) {
            queueNumber = context.getParameter("visitNumber");
        }

        if (isBlank(queueNumber)) {
            throw new ObjectNotFoundException("ticketNumber or visitNumber is required");
        }

        Date now = new Date();
        Date startOfDay = OpenmrsUtil.firstSecondOfDay(now);
        Date endOfDay = OpenmrsUtil.getLastMomentOfDay(now);

        List<QueueKioskEntry> results = patientQueueService()
                .getPatientQueueByVisitNumber(queueNumber, startOfDay, endOfDay)
                .stream()
                .map(QueueKioskEntry::new).collect(Collectors.toList());

        if (results.isEmpty()) {
            results=ugandaEMRService()
                    .getQueueEntryByTicketNumber(queueNumber, startOfDay, endOfDay)
                    .stream()
                    .map(QueueKioskEntry::new)
                    .collect(Collectors.toList());
        }

        if (results.isEmpty()) {
            throw new ObjectNotFoundException("No queue entry found for: " + queueNumber);
        }

        return new NeedsPaging<QueueKioskEntry>(results, context);
    }

    @Override
    public QueueKioskEntry create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("QueueKioskResource does not support create");
    }

    @Override
    public QueueKioskEntry save(QueueKioskEntry delegate) {
        throw new ResourceDoesNotSupportOperationException("QueueKioskResource does not support save");
    }

    @Override
    public SimpleObject update(String uuid, SimpleObject propertiesToUpdate, RequestContext context)
            throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("QueueKioskResource does not support update");
    }

    @Override
    public SimpleObject getAll(RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("QueueKioskResource does not support getAll");
    }

    @Override
    protected void delete(QueueKioskEntry delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("QueueKioskResource does not support delete");
    }

    @Override
    public void purge(QueueKioskEntry delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("QueueKioskResource does not support purge");
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription d = new DelegatingResourceDescription();

        d.addProperty("uuid");
        d.addProperty("ticketNumber");
        d.addProperty("status");
        d.addProperty("displayName");
        d.addProperty("queueType");
        d.addProperty("locationTo", Representation.REF);
        d.addProperty("queueRoom", Representation.REF);
        d.addProperty("datePicked");
        d.addProperty("dateCompleted");
        d.addProperty("dateCancelled");
        d.addProperty("dateCreated");

        if (rep instanceof FullRepresentation) {
            d.addProperty("dateChanged");
            d.addProperty("patient", Representation.REF);
            d.addProperty("patientQueue");
            d.addProperty("nonPatientQueue");
            d.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
        }

        d.addSelfLink();
        return d;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        return null;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}