package org.openmrs.module.ugandaemr.web.resource;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.RefProperty;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientqueueing.api.PatientQueueingService;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.module.ugandaemr.web.customdto.PatientQueueStatistic;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.DateUtil;
import org.openmrs.util.OpenmrsUtil;

import java.text.SimpleDateFormat;
import java.util.*;

@Resource(name = RestConstants.VERSION_1 + "/queuestatistics", supportedClass = PatientQueueStatistic.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class QueueStatisticResource extends DelegatingCrudResource<PatientQueueStatistic> {

    @Override
    public PatientQueueStatistic newDelegate() {
        return new PatientQueueStatistic();
    }

    @Override
    public PatientQueueStatistic save(PatientQueueStatistic PatientQueueStatistic) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public PatientQueueStatistic getByUniqueId(String uniqueId) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public NeedsPaging<PatientQueueStatistic> doGetAll(RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public List<Representation> getAvailableRepresentations() {
        return Arrays.asList(Representation.DEFAULT, Representation.FULL);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("locationTag", Representation.REF);
            description.addProperty("pending");
            description.addProperty("serving");
            description.addProperty("completed");
            description.addSelfLink();
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("locationTag", Representation.FULL);
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("locationTag");
            description.addProperty("pending");
            description.addProperty("serving");
            description.addProperty("completed");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    protected void delete(PatientQueueStatistic syncFhirCase, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public void purge(PatientQueueStatistic syncFhirCase, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }


    @Override
    protected PageableResult doSearch(RequestContext context) {
        PatientQueueingService patientQueueingService = Context.getService(PatientQueueingService.class);
        LocationService locationService = Context.getLocationService();

        // Extract and parse request parameters
        String locationParam = context.getParameter("parentLocation");
        Date fromDate = parseDateParam(context, "fromDate", true);
        Date toDate = parseDateParam(context, "toDate", false);
        boolean onlyInQueueRooms = Boolean.parseBoolean(context.getParameter("onlyInQueueRooms"));

        // Resolve location
        Location location = locationService.getLocationByUuid(locationParam);
        List<PatientQueueStatistic> statistics = new ArrayList<>();

        if (location != null) {
            List<PatientQueue> patientQueues = patientQueueingService.getPatientQueueByParentLocation(
                    location, null, fromDate, toDate, onlyInQueueRooms
            );

            for (LocationTag locationTag : getServiceAreaTags()) {
                PatientQueueStatistic stat = new PatientQueueStatistic();
                stat.setLocationTag(locationTag);
                stat.setUuid(locationTag.getUuid());
                stat.setPending(0);
                stat.setServing(0);
                stat.setCompleted(0);

                if (patientQueues != null) {
                    for (PatientQueue queue : patientQueues) {
                        if (hasTag(queue.getQueueRoom(), locationTag) || hasTag(queue.getLocationTo(), locationTag)) {
                            switch (queue.getStatus()) {
                                case PENDING:   stat.setPending(stat.getPending() + 1); break;
                                case PICKED:    stat.setServing(stat.getServing() + 1); break;
                                case COMPLETED: stat.setCompleted(stat.getCompleted() + 1); break;
                            }
                        }
                    }
                }

                statistics.add(stat);
            }
        }

        return new NeedsPaging<>(statistics, context);
    }


    @Override
    public Model getGETModel(Representation rep) {
        ModelImpl model = (ModelImpl) super.getGETModel(rep);
        if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
            model.property("locationTag", new RefProperty("#/definitions/LocationTagGetRef"))
                    .property("pending", new IntegerProperty())
                    .property("serving", new IntegerProperty())
                    .property("completed", new IntegerProperty());
        }
        if (rep instanceof DefaultRepresentation) {
            model.property("locationTag", new RefProperty("#/definitions/LocationTagGetRef"))
                    .property("pending", new IntegerProperty())
                    .property("serving", new IntegerProperty())
                    .property("completed", new IntegerProperty());

        } else if (rep instanceof FullRepresentation) {
            model.property("locationTag", new RefProperty("#/definitions/LocationTagGetRef"))
                    .property("pending", new IntegerProperty())
                    .property("serving", new IntegerProperty())
                    .property("completed", new IntegerProperty());
        }
        return model;
    }


    private Date getDateFromString(String dateString, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            // Parse the string and convert it to a Date object
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            log.error(e);
        }
        return new Date();
    }

    /**
     * gets  a list of location tags which represent service areas
     *
     * @return a list of tags of service locations
     */
    private List<LocationTag> getServiceAreaTags() {
        List<LocationTag> locationTags = new ArrayList<>();
        List<String> locationTagUuid = Arrays.asList(Context.getAdministrationService()
                .getGlobalProperty("patientqueueing.locationTagPatientQueueCategory").split(","));
        locationTagUuid.forEach(uuid -> {
            LocationTag locationTag = Context.getLocationService().getLocationTagByUuid(uuid);
            if (locationTag != null) {
                locationTags.add(locationTag);
            }
        });
        return locationTags;
    }

    private Date parseDateParam(RequestContext context, String param, boolean isStartOfDay) {
        String value = context.getParameter(param);
        if (value == null) return null;

        Date parsed = getDateFromString(value, "yyyy-MM-dd");
        return isStartOfDay ? OpenmrsUtil.firstSecondOfDay(parsed) : OpenmrsUtil.getLastMomentOfDay(parsed);
    }

    private boolean hasTag(Location location, LocationTag tag) {
        return location != null && location.getTags() != null && location.getTags().contains(tag);
    }
}
