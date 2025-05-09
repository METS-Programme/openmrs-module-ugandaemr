package org.openmrs.module.ugandaemr.web.resource;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.ugandaemr.api.queuemapper.CheckInPatient;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
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
import org.openmrs.module.webservices.validation.ValidateUtil;

import java.util.Arrays;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/checkinpatient", supportedClass = CheckInPatient.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class CheckInPatientResource extends DelegatingCrudResource<CheckInPatient> {

    @Override
    public CheckInPatient newDelegate() {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public CheckInPatient save(CheckInPatient TestResult) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
        // Retrieve required services once
        PatientService patientService = Context.getPatientService();
        LocationService locationService = Context.getLocationService();
        ProviderService providerService = Context.getProviderService();
        UgandaEMRService ugandaEMRService = Context.getService(UgandaEMRService.class);

        // Extract required properties
        String patientUuid = getRequiredProperty(propertiesToCreate, "patient");
        String currentLocationUuid = getRequiredProperty(propertiesToCreate, "currentLocation");
        String locationToUuid = getRequiredProperty(propertiesToCreate, "locationTo");
        String queueRoomUuid = getRequiredProperty(propertiesToCreate, "queueRoom");
        String providerUuid = getRequiredProperty(propertiesToCreate, "provider");
        String patientStatus = getRequiredProperty(propertiesToCreate, "patientStatus");
        String visitType = getRequiredProperty(propertiesToCreate, "visitType");
        String visitComment = propertiesToCreate.get("visitComment");
        Integer priority = null;

        // Retrieve entities from UUIDs
        Patient patient = patientService.getPatientByUuid(patientUuid);
        Location currentLocation = locationService.getLocationByUuid(currentLocationUuid);
        Location locationTo = locationService.getLocationByUuid(locationToUuid);
        Location queueRoom = locationService.getLocationByUuid(queueRoomUuid);
        Provider provider = providerService.getProviderByUuid(providerUuid);

        if (propertiesToCreate.get("priority") != null) {
            try {
                priority = Integer.parseInt(propertiesToCreate.get("priority").toString());
            } catch (Exception exception) {
                log.error("Unparsable value from field priority",exception);
            }
        }

        // Validate entity existence
        validateNotNull(patient, "Patient not found for UUID: " + patientUuid);
        validateNotNull(currentLocation, "Current Location not found for UUID: " + currentLocationUuid);
        validateNotNull(locationTo, "LocationTo not found for UUID: " + locationToUuid);
        validateNotNull(queueRoom, "Queue Room not found for UUID: " + queueRoomUuid);
        validateNotNull(provider, "Provider not found for UUID: " + providerUuid);

        // Perform check-in
        CheckInPatient delegate = ugandaEMRService.checkInPatient(patient, currentLocation, locationTo, queueRoom, provider, visitComment, patientStatus, visitType,priority);

        // Validate result
        ValidateUtil.validate(delegate);

        // Convert result to the required representation
        SimpleObject response = (SimpleObject) ConversionUtil.convertToRepresentation(delegate, context.getRepresentation());

        // Add type if necessary
        if (hasTypesDefined()) {
            response.add(RestConstants.PROPERTY_FOR_TYPE, getTypeName(delegate));
        }

        return response;
    }

    @Override
    public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public CheckInPatient getByUniqueId(String uniqueId) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public NeedsPaging<CheckInPatient> doGetAll(RequestContext context) throws ResponseException {
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
            description.addProperty("visit");
            description.addProperty("patientQueue");
            description.addSelfLink();
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("visit", Representation.REF);
            description.addProperty("patientQueue", Representation.REF);
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("visit", Representation.REF);
            description.addProperty("patientQueue", Representation.REF);
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    protected void delete(CheckInPatient TestResult, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public void purge(CheckInPatient TestResult, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("visit");
        return description;
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    /**
     * Helper method to retrieve a required property.
     */
    private String getRequiredProperty(SimpleObject properties, String key) {
        String value = properties.get(key);
        if (value == null) {
            throw new IllegalArgumentException(key + " cannot be null");
        }
        return value;
    }

    /**
     * Helper method to validate that an object is not null.
     */
    private void validateNotNull(Object obj, String errorMessage) {
        if (obj == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
