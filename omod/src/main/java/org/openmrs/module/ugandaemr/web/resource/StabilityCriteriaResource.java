package org.openmrs.module.ugandaemr.web.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.ugandaemr.api.dto.StabilityCriteria;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Resource(name = RestConstants.VERSION_1 + "/stabilitycriteria", supportedClass = StabilityCriteria.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class StabilityCriteriaResource extends DelegatingCrudResource<StabilityCriteria> {

    private static final Log log = LogFactory.getLog(StabilityCriteriaResource.class);

    @Override
    public StabilityCriteria newDelegate() {
        return new StabilityCriteria();
    }

    @Override
    public StabilityCriteria save(StabilityCriteria duplicateEncounter) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public StabilityCriteria getByUniqueId(String uniqueId) {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    protected void delete(StabilityCriteria StabilityCriteria, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public void purge(StabilityCriteria StabilityCriteria, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("Operation not supported");
    }

    @Override
    public NeedsPaging<StabilityCriteria> doGetAll(RequestContext context) throws ResponseException {
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
            description.addProperty("uuid");
            description.addProperty("vlObs", Representation.REF);
            description.addProperty("vlDateObs", Representation.REF);
            description.addProperty("artStartDate");
            description.addProperty("regimenObs", Representation.REF);
            description.addProperty("regimenObsConceptId");
            description.addProperty("currentRegimenObs", Representation.REF);
            description.addProperty("currentRegimenObsConceptId");
            description.addProperty("regimenBeforeDTGObs", Representation.REF);
            description.addProperty("regimenBeforeDTGObsValueConceptId");
            description.addProperty("onThirdRegimen");
            description.addProperty("adherenceObs", Representation.REF);
            description.addProperty("conceptForClinicStage");
            description.addProperty("sputumResultDateObs", Representation.REF);
            description.addProperty("sputumResultObs", Representation.REF);
            description.addProperty("sputumResultObsValueConceptId");
            description.addProperty("baselineRegimenConceptId");
            description.addProperty("enableCliniciansMakeStabilityDecisions");

            description.addSelfLink();

            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("vlObs", Representation.FULL);
            description.addProperty("vlDateObs", Representation.FULL);
            description.addProperty("artStartDate");
            description.addProperty("regimenObs", Representation.FULL);
            description.addProperty("regimenObsConceptId");
            description.addProperty("currentRegimenObs", Representation.FULL);
            description.addProperty("currentRegimenObsConceptId");
            description.addProperty("regimenBeforeDTGObs", Representation.FULL);
            description.addProperty("regimenBeforeDTGObsValueConceptId");
            description.addProperty("onThirdRegimen");
            description.addProperty("adherenceObs", Representation.FULL);
            description.addProperty("conceptForClinicStage");
            description.addProperty("sputumResultDateObs", Representation.FULL);
            description.addProperty("sputumResultObs", Representation.FULL);
            description.addProperty("sputumResultObsValueConceptId");
            description.addProperty("baselineRegimenConceptId");
            description.addProperty("enableCliniciansMakeStabilityDecisions");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("vlObs", Representation.DEFAULT);
            description.addProperty("vlDateObs", Representation.DEFAULT);
            description.addProperty("artStartDate");
            description.addProperty("regimenObs", Representation.DEFAULT);
            description.addProperty("regimenObsConceptId");
            description.addProperty("currentRegimenObs", Representation.DEFAULT);
            description.addProperty("currentRegimenObsConceptId");
            description.addProperty("regimenBeforeDTGObs", Representation.DEFAULT);
            description.addProperty("regimenBeforeDTGObsValueConceptId");
            description.addProperty("onThirdRegimen");
            description.addProperty("adherenceObs", Representation.DEFAULT);
            description.addProperty("conceptForClinicStage");
            description.addProperty("sputumResultDateObs", Representation.DEFAULT);
            description.addProperty("sputumResultObs", Representation.DEFAULT);
            description.addProperty("sputumResultObsValueConceptId");
            description.addProperty("baselineRegimenConceptId");
            description.addProperty("enableCliniciansMakeStabilityDecisions");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        Encounter encounter = null;
        Patient patient = null;
        Visit visit = null;

        if (context.getParameter("visit") != null && !context.getParameter("visit").equals("null")) {
            if (isUuid(context.getParameter("visit"))) {
                visit = Context.getVisitService().getVisitByUuid(context.getParameter("visit"));
            } else {
                visit = Context.getVisitService().getVisit(Integer.parseInt(context.getParameter("visit")));
            }
        }
        if (context.getParameter("patient") != null) {
            if (isUuid(context.getParameter("patient"))) {
                patient = Context.getPatientService().getPatientByUuid(context.getParameter("patient"));
            } else {
                patient = Context.getPatientService().getPatient(Integer.parseInt(context.getParameter("patient")));
            }
        }
        if (context.getParameter("encounter") != null && !context.getParameter("encounter").equals("null")) {
            if (isUuid(context.getParameter("encounter"))) {
                encounter = Context.getEncounterService().getEncounterByUuid(context.getParameter("encounter"));
            } else {
                encounter = Context.getEncounterService().getEncounter((Integer.parseInt(context.getParameter("encounter"))));
            }
        }

        // Delegate to service layer for business logic
        UgandaEMRService ugandaEMRService = Context.getService(UgandaEMRService.class);
        StabilityCriteria stabilityCriteria = ugandaEMRService.generateStabilityCriteria(patient, encounter, visit);

        return new NeedsPaging<StabilityCriteria>(Collections.singletonList(stabilityCriteria), context);
    }

    private Boolean isUuid(String params) {
        String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(params);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}