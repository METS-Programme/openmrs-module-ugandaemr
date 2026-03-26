package org.openmrs.module.ugandaemr.web.resource;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Provider;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.ugandaemr.api.queuemapper.CheckInPatient;
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
import org.openmrs.module.webservices.validation.ValidateUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/selfcheckinpatient", supportedClass = CheckInPatient.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class SelfCheckInPatientResource extends DelegatingCrudResource<CheckInPatient> {

    private static final String PHONE_ATTRIBUTE_TYPE_UUID = "14d4f066-15f5-102d-96e4-000c29c2a5d7";
    private static final String NATIONAL_ID_IDENTIFIER_TYPE_UUID = "f0c16a6d-dc5f-4118-a803-616d0075d282";
    private static final String PATIENT_ID_IDENTIFIER_TYPE_UUID = "e1731641-30ab-102d-86b0-7a5022ba4115";

    @Override
    public CheckInPatient newDelegate() {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public CheckInPatient save(CheckInPatient delegate) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
        PatientService patientService = Context.getPatientService();
        PersonService personService = Context.getPersonService();
        LocationService locationService = Context.getLocationService();
        ProviderService providerService = Context.getProviderService();
        UgandaEMRService ugandaEMRService = Context.getService(UgandaEMRService.class);

        String phoneNumber = trimToNull(propertiesToCreate.get("phoneNumber"));
        String nationalId = trimToNull(propertiesToCreate.get("nationalId"));
        String patientId = trimToNull(propertiesToCreate.get("patientId"));

        if (phoneNumber == null && nationalId == null && patientId == null) {
            throw new IllegalArgumentException("At least one of phoneNumber, nationalId, or patientId is required");
        }

        String currentLocationUuid = getRequiredProperty(propertiesToCreate, "currentLocation");
        String locationToUuid = getRequiredProperty(propertiesToCreate, "locationTo");
        String queueRoomUuid = getRequiredProperty(propertiesToCreate, "queueRoom");
        String providerUuid = trimToNull(propertiesToCreate.get("provider"));
        String patientStatus = getRequiredProperty(propertiesToCreate, "patientStatus");
        String visitType = getRequiredProperty(propertiesToCreate, "visitType");
        String visitComment = trimToNull(propertiesToCreate.get("visitComment"));
        Integer priority = parsePriority(propertiesToCreate.get("priority"));

        Patient patient = resolvePatient(patientService, personService, phoneNumber, nationalId, patientId);
        validateNotNull(patient, "No patient found matching the provided details");

        Location currentLocation = locationService.getLocationByUuid(currentLocationUuid);
        Location locationTo = locationService.getLocationByUuid(locationToUuid);
        Location queueRoom = locationService.getLocationByUuid(queueRoomUuid);

        validateNotNull(currentLocation, "Current Location not found for UUID: " + currentLocationUuid);
        validateNotNull(locationTo, "LocationTo not found for UUID: " + locationToUuid);
        validateNotNull(queueRoom, "Queue Room not found for UUID: " + queueRoomUuid);

        Provider provider;
        if (StringUtils.isNotBlank(providerUuid)) {
            provider = providerService.getProviderByUuid(providerUuid);
        } else {
            provider = ugandaEMRService.getLeastBusyProviderForLocation(locationTo);
        }

        validateNotNull(provider, "No provider found for location: " + locationTo.getName());

        CheckInPatient delegate = ugandaEMRService.checkInPatient(patient, currentLocation, locationTo, queueRoom, provider, visitComment, patientStatus, visitType, priority);

        ValidateUtil.validate(delegate);

        SimpleObject response = (SimpleObject) ConversionUtil.convertToRepresentation(delegate, context.getRepresentation());

        if (hasTypesDefined()) {
            response.add(RestConstants.PROPERTY_FOR_TYPE, getTypeName(delegate));
        }

        return response;
    }

    private Patient resolvePatient(PatientService patientService, PersonService personService,
                                   String phoneNumber, String nationalId, String patientId) {

        if (StringUtils.isNotBlank(patientId)) {
            Patient patient = getPatientByIdentifier(patientService, patientId, PATIENT_ID_IDENTIFIER_TYPE_UUID);
            if (patient != null) {
                return patient;
            }
        }

        if (StringUtils.isNotBlank(nationalId)) {
            Patient patient = getPatientByIdentifier(patientService, nationalId, NATIONAL_ID_IDENTIFIER_TYPE_UUID);
            if (patient != null) {
                return patient;
            }
        }

        if (StringUtils.isNotBlank(phoneNumber)) {
            Patient patient = getPatientByPhoneNumber(patientService, personService, phoneNumber);
            if (patient != null) {
                return patient;
            }
        }

        return null;
    }

    private Patient getPatientByIdentifier(PatientService patientService, String identifierValue, String identifierTypeUuid) {
        PatientIdentifierType identifierType = patientService.getPatientIdentifierTypeByUuid(identifierTypeUuid);
        validateNotNull(identifierType, "PatientIdentifierType not found for UUID: " + identifierTypeUuid);

        List<PatientIdentifier> identifiers = patientService.getPatientIdentifiers(
                identifierValue, Arrays.asList(identifierType), null, null, null
        );

        if (identifiers == null || identifiers.isEmpty()) {
            return null;
        }

        if (identifiers.size() > 1) {
            throw new IllegalArgumentException("Multiple patients found for identifier: " + identifierValue);
        }

        return identifiers.get(0).getPatient();
    }

    private Patient getPatientByPhoneNumber(PatientService patientService, PersonService personService, String phoneNumber) {
      UgandaEMRService service=Context.getService(UgandaEMRService.class);

        List<Patient> patients = service.getPatientByPhoneNumber(phoneNumber);
        if (patients == null || patients.isEmpty()) {
            return null;
        }

        if (patients.size() > 1) {
            throw new IllegalArgumentException("Multiple patients found for phone number: " + phoneNumber);
        }

        return patients.get(0);
    }

    private String getPhoneNumber(Person person) {
        if (person == null) {
            return null;
        }

        PersonAttributeType attributeType = Context.getPersonService().getPersonAttributeTypeByUuid(PHONE_ATTRIBUTE_TYPE_UUID);
        if (attributeType == null) {
            return null;
        }

        PersonAttribute attribute = person.getAttribute(attributeType);
        return attribute != null ? attribute.getValue() : null;
    }

    private String getIdentifier(Patient patient, String identifierTypeUuid) {
        if (patient == null) {
            return null;
        }

        PatientIdentifierType type = Context.getPatientService().getPatientIdentifierTypeByUuid(identifierTypeUuid);
        if (type == null) {
            return null;
        }

        PatientIdentifier identifier = patient.getPatientIdentifier(type);
        return identifier != null ? identifier.getIdentifier() : null;
    }

    private Integer parsePriority(Object priorityValue) {
        if (priorityValue == null) {
            return null;
        }

        try {
            return Integer.parseInt(priorityValue.toString());
        } catch (Exception e) {
            log.error("Unparsable value from field priority", e);
            return null;
        }
    }

    private String getRequiredProperty(SimpleObject properties, String key) {
        String value = properties.get(key);
        if (value == null) {
            throw new IllegalArgumentException(key + " cannot be null");
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

    private void validateNotNull(Object obj, String errorMessage) {
        if (obj == null) {
            throw new IllegalArgumentException(errorMessage);
        }
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
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("visit");
        description.addProperty("patientQueue");
        description.addProperty("patient");
        description.addSelfLink();

        if (rep instanceof FullRepresentation) {
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
        }

        return description;
    }

    @Override
    protected void delete(CheckInPatient delegate, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public void purge(CheckInPatient delegate, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("phoneNumber");
        description.addProperty("nationalId");
        description.addProperty("patientId");
        description.addProperty("currentLocation");
        description.addProperty("locationTo");
        description.addProperty("queueRoom");
        description.addProperty("provider");
        description.addProperty("patientStatus");
        description.addProperty("visitType");
        description.addProperty("visitComment");
        description.addProperty("priority");
        return description;
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }
}