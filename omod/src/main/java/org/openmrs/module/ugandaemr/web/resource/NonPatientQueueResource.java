package org.openmrs.module.ugandaemr.web.resource;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Provider;
import org.openmrs.api.LocationService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.PublicHoliday;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.ugandaemr.api.model.NonPatientQueue;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/nonpatientqueue", supportedClass = NonPatientQueue.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class NonPatientQueueResource extends DelegatingCrudResource<NonPatientQueue> {

    @Override
    public NonPatientQueue newDelegate() {
        return new NonPatientQueue();
    }

    @Override
    public NonPatientQueue save(NonPatientQueue delegate) {
        return Context.getService(UgandaEMRService.class).saveQueueEntry(delegate);
    }

    @Override
    public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
        UgandaEMRService service = Context.getService(UgandaEMRService.class);
        LocationService locationService = Context.getLocationService();

        String displayName = trimToNull(propertiesToCreate.get("displayName"));
        String phoneNumber = trimToNull(propertiesToCreate.get("phoneNumber"));
        String queueType = getRequiredProperty(propertiesToCreate, "queueType");
        String currentLocationUuid = getRequiredProperty(propertiesToCreate, "currentLocation");
        String locationToUuid = getRequiredProperty(propertiesToCreate, "locationTo");
        String queueRoomUuid = getRequiredProperty(propertiesToCreate, "queueRoom");
        Integer priority = parseInteger(propertiesToCreate.get("priority"));
        String comment = trimToNull(propertiesToCreate.get("comment"));

        Location currentLocation = locationService.getLocationByUuid(currentLocationUuid);
        Location locationTo = locationService.getLocationByUuid(locationToUuid);
        Location queueRoom = locationService.getLocationByUuid(queueRoomUuid);

        validateNotNull(currentLocation, "Current location not found");
        validateNotNull(locationTo, "Destination location not found");
        validateNotNull(queueRoom, "Queue room not found");

        NonPatientQueue delegate = service.createQueueEntry(
                displayName,
                phoneNumber,
                NonPatientQueue.NonPatientQueueType.fromString(queueType),
                currentLocation,
                locationTo,
                queueRoom,
                priority,
                comment
        );

        SimpleObject response = (SimpleObject) ConversionUtil.convertToRepresentation(delegate, context.getRepresentation());

        if (hasTypesDefined()) {
            response.add(RestConstants.PROPERTY_FOR_TYPE, getTypeName(delegate));
        }

        return response;
    }

    @Override
    public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
        UgandaEMRService service = Context.getService(UgandaEMRService.class);
        ProviderService providerService = Context.getProviderService();

        NonPatientQueue queue = service.getQueueEntryByUuid(uuid);
        validateNotNull(queue, "Queue entry not found");

        String action = trimToNull(propertiesToUpdate.get("action"));
        String providerUuid = trimToNull(propertiesToUpdate.get("provider"));
        Provider provider = providerUuid != null ? providerService.getProviderByUuid(providerUuid) : null;

        if ("call".equalsIgnoreCase(action)) {
            return service.callQueueEntry(queue, provider);
        }
        if ("arrive".equalsIgnoreCase(action)) {
            return service.markArrived(queue);
        }
        if ("start".equalsIgnoreCase(action)) {
            return service.startServing(queue, provider);
        }
        if ("complete".equalsIgnoreCase(action)) {
            return service.completeQueueEntry(queue, provider);
        }

        throw new IllegalArgumentException("Unsupported action: " + action);
    }

    @Override
    public NonPatientQueue getByUniqueId(String uniqueId) {
        return Context.getService(UgandaEMRService.class).getQueueEntryByUuid(uniqueId);
    }

    @Override
    public NeedsPaging<NonPatientQueue> doGetAll(RequestContext context) {
        return new NeedsPaging<NonPatientQueue>(new ArrayList<NonPatientQueue>(Context.getService(UgandaEMRService.class).getAllActiveQueueEntries()),
                context);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {

        List<NonPatientQueue> nonPatientQueues = Context.getService(UgandaEMRService.class).getAllActiveQueueEntries();

        return new NeedsPaging<NonPatientQueue>(new ArrayList<NonPatientQueue>(),
                context);
    }

    @Override
    public List<Representation> getAvailableRepresentations() {
        return Arrays.asList(Representation.DEFAULT, Representation.FULL);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription d = new DelegatingResourceDescription();
        d.addProperty("uuid");
        d.addProperty("ticketNumber");
        d.addProperty("displayName");
        d.addProperty("phoneNumber");
        d.addProperty("queueType");
        d.addProperty("status");
        d.addProperty("currentLocation");
        d.addProperty("locationTo");
        d.addProperty("queueRoom");
        d.addProperty("priority");
        d.addProperty("comment");
        d.addProperty("calledAt");
        d.addProperty("arrivedAt");
        d.addProperty("startedAt");
        d.addProperty("endedAt");
        d.addSelfLink();

        if (rep instanceof FullRepresentation) {
            d.addProperty("calledBy");
            d.addProperty("servedBy");
        }

        return d;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription d = new DelegatingResourceDescription();
        d.addProperty("displayName");
        d.addProperty("phoneNumber");
        d.addProperty("queueType");
        d.addProperty("currentLocation");
        d.addProperty("locationTo");
        d.addProperty("queueRoom");
        d.addProperty("priority");
        d.addProperty("comment");
        return d;
    }

    @Override
    protected void delete(NonPatientQueue delegate, String reason, RequestContext context) {
        Context.getService(UgandaEMRService.class).voidQueueEntry(delegate, reason);
    }

    @Override
    public void purge(NonPatientQueue delegate, RequestContext context) {
        throw new ResourceDoesNotSupportOperationException("Purge not supported");
    }

    private String getRequiredProperty(SimpleObject properties, String key) {
        String value = trimToNull(properties.get(key));
        if (value == null) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value;
    }

    private String trimToNull(Object value) {
        if (value == null) {
            return null;
        }
        String s = value.toString().trim();
        return s.isEmpty() ? null : s;
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return null;
        }
        return Integer.valueOf(value.toString());
    }

    private void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}