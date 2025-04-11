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
        PatientService patientService = Context.getPatientService();
        LocationService locationService = Context.getLocationService();
        ProviderService providerService = Context.getProviderService();

        Patient patient = patientService.getPatientByUuid(propertiesToCreate.get("patient"));
        Location currentLocation = locationService.getLocationByUuid(propertiesToCreate.get("currentLocation"));
        Location locationTo = locationService.getLocationByUuid(propertiesToCreate.get("locationTo"));
        Location queueRoom = locationService.getLocationByUuid(propertiesToCreate.get("queueRoom"));
        Provider provider = providerService.getProviderByUuid(propertiesToCreate.get("provider"));
        String visitComment = propertiesToCreate.get("visitComment");
        String patientStatus = propertiesToCreate.get("patientStatus");
        String visitType = propertiesToCreate.get("visitType");

        UgandaEMRService ugandaEMRService = Context.getService(UgandaEMRService.class);

        CheckInPatient delegate = ugandaEMRService.checkInPatient(patient, currentLocation,locationTo, queueRoom, provider, visitComment, patientStatus, visitType);

        ValidateUtil.validate(delegate);
        SimpleObject ret = (SimpleObject) ConversionUtil.convertToRepresentation(delegate, context.getRepresentation());

        if (hasTypesDefined()) {
            ret.add(RestConstants.PROPERTY_FOR_TYPE, getTypeName(delegate));
        }

        return ret;
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
}
