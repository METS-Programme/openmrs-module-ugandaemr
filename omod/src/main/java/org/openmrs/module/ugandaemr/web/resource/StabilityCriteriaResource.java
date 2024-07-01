package org.openmrs.module.ugandaemr.web.resource;

import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.web.customdto.StabilityCriteria;
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


import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Resource(name = RestConstants.VERSION_1 + "/stabilitycriteria", supportedClass = StabilityCriteria.class, supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class StabilityCriteriaResource extends DelegatingCrudResource<StabilityCriteria> {

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


        StabilityCriteria stabilityCriteria = generateStabilityCriteria(patient, encounter, visit);


        return new NeedsPaging<StabilityCriteria>(Collections.singletonList(stabilityCriteria), context);
    }


    private StabilityCriteria generateStabilityCriteria(Patient patient, Encounter encounter, Visit visit) {
        StabilityCriteria stabilityCriteria = newDelegate();
        String allowCliniciansToMakeDecisionOnDSDM = Context.getAdministrationService().getGlobalProperty("ugandaemr.dsdm.allowClinicalOverrideDSDMPatientStability");
        if (allowCliniciansToMakeDecisionOnDSDM.equals("false")) {
            Integer baselineRegimenConceptId = 99061;
            Integer currentRegimenConceptId = 90315;
            ConceptService conceptService = Context.getConceptService();
            Integer minimumDurationOnCurrentRegimen = Integer.parseInt(Context.getAdministrationService().getGlobalProperty("ugandaemr.dsdm.currentRegimenDurationRequirementInMonths"));

            Visit encounterVisit = new Visit();
            if (visit == null && encounter != null) {
                encounterVisit = encounter.getVisit();
            } else if (visit != null) {
                encounterVisit = visit;
            }


            /**
             * Last Viral Load
             */
            List<Obs> vlDateObsList = getObsListFromIdList("SELECT obs_id FROM obs where obs.person_id='" + patient.getPatientId() + "' AND obs.concept_id = 163023 AND obs.voided = false  ORDER BY  obs.encounter_id DESC LIMIT 1");

            if (vlDateObsList.size() > 0) {
                List<Obs> vlObsList = getObsListFromIdList("SELECT obs_id FROM obs where obs.person_id='" + patient.getPatientId() + "' AND obs.concept_id = 856 AND encounter_id='" + vlDateObsList.get(0).getEncounter().getEncounterId() + "' AND obs.voided = false  ORDER BY  obs.encounter_id DESC LIMIT 1");

                if (vlObsList.size() > 0) {
                    stabilityCriteria.setVlObs(vlObsList.get(0));
                    stabilityCriteria.setVlDateObs(vlDateObsList.get(0));

                } else {
                    stabilityCriteria.setVlObs(null);
                    stabilityCriteria.setVlDateObs(null);
                }
            } else {
                stabilityCriteria.setVlObs(null);
                stabilityCriteria.setVlDateObs(null);
            }

            List<Person> personList = new ArrayList<>();
            personList.add(patient.getPerson());

            /**
             * Current regimen
             */
            int monthOffSet = -minimumDurationOnCurrentRegimen;

            Obs obs = getMostRecentObservation(encounterVisit, "90315");

            String query = "";
            String queryCurrentRegimen = "";
            List<Obs> regimenObsList = new ArrayList<>();
            List<Obs> currentRegimenList = new ArrayList<>();

            //Check if Obs of Regimen is on not Null
            if (obs != null) {
                //Check if Obs conceptId is the same as the art encounter regimen concept
                // Check if regimen is a DTG regimen
                if (checkIfDTG(obs)) {
                    query = "SELECT obs_id FROM obs where  obs.obs_datetime <= DATE('" + encounterVisit.getStartDatetime() + "') AND obs.person_id='" + patient.getPatientId() + "' AND concept_id=" + currentRegimenConceptId + " AND obs.voided = false ORDER BY  obs.obs_datetime DESC";
                } else {
                    query = "SELECT obs_id FROM obs where  obs.obs_datetime <= DATE('" + getDateBefore(encounterVisit.getStartDatetime(), monthOffSet, 0) + "') AND obs.person_id='" + patient.getPatientId() + "' AND obs.value_coded = " + obs.getValueCoded().getConceptId() + " AND obs.voided = false ORDER BY  obs.encounter_id DESC";
                }
                regimenObsList = getObsListFromIdList(query);

                queryCurrentRegimen = "SELECT obs_id FROM obs where  obs.person_id='" + patient.getPatientId() + "' AND obs.obs_datetime <= DATE('" + encounterVisit.getStartDatetime() + "') AND obs.value_coded = " + obs.getValueCoded().getConceptId() + " AND obs.voided = false ORDER BY  obs.obs_datetime ASC LIMIT 0,1";

                currentRegimenList = getObsListFromIdList(queryCurrentRegimen);
            }


            if (regimenObsList.size() > 0) {
                if (checkIfDTG(regimenObsList.get(0)) && regimenObsList.size() > 1) {
                    List<Obs> regimenBeforeDTGObs = getObsListFromIdList("SELECT obs_id FROM obs where  obs.obs_datetime <= DATE('" + getDateBefore(encounterVisit.getStartDatetime(), -12, 0) + "') AND obs.person_id='" + patient.getPatientId() + "' AND obs.concept_id = " + currentRegimenConceptId + " AND obs.voided = false ORDER BY  obs.encounter_id DESC");
                    if (regimenBeforeDTGObs.size() > 0) {
                        stabilityCriteria.setRegimenBeforeDTGObs(regimenBeforeDTGObs.get(0));
                        stabilityCriteria.setRegimenBeforeDTGObsValueConceptId(regimenBeforeDTGObs.get(0).getValueCoded().getConceptId());
                    } else {
                        stabilityCriteria.setRegimenBeforeDTGObs(null);
                    }
                } else {
                    stabilityCriteria.setRegimenBeforeDTGObs(null);
                }
                stabilityCriteria.setRegimenObs(regimenObsList.get(0));
                stabilityCriteria.setRegimenObsConceptId(regimenObsList.get(0).getConcept().getConceptId());
            } else {
                stabilityCriteria.setRegimenObs(null);
                stabilityCriteria.setRegimenBeforeDTGObs(null);
            }

            if (currentRegimenList.size() > 0) {
                stabilityCriteria.setCurrentRegimenObs(currentRegimenList.get(0));
                stabilityCriteria.setCurrentRegimenObsConceptId(currentRegimenList.get(0).getConcept().getConceptId());
            } else {
                stabilityCriteria.setCurrentRegimenObs(null);
            }
            stabilityCriteria.setBaselineRegimenConceptId(baselineRegimenConceptId);

            /**
             * Adherence
             */
            List<Obs> adherenceObsList = getObsListFromIdList("SELECT obs_id FROM obs where  obs.obs_datetime BETWEEN '" + getDateBefore(encounterVisit.getStartDatetime(), -6, 0) + "' AND '" + encounterVisit.getStartDatetime() + "' AND obs.person_id='" + patient.getPatientId() + "' AND obs.concept_id = 90221 AND obs.voided = false ORDER BY  obs.encounter_id DESC");

            if (adherenceObsList.size() > 0) {
                stabilityCriteria.setAdherenceObs(adherenceObsList);
            } else {
                stabilityCriteria.setAdherenceObs(null);
            }

            /**
             * ThirdLine Regimen
             */
            List<Concept> concepts = new ArrayList<>();
            Collection<ConceptAnswer> conceptAnswers = conceptService.getConcept(1).getAnswers(false);
            for (ConceptAnswer conceptAnswer : conceptAnswers) {
                if (conceptAnswer.getConcept().getConceptId() != 90002) {
                    conceptAnswers.remove(conceptAnswer);
                    concepts.add(conceptAnswer.getAnswerConcept());
                }
            }
            if (regimenObsList.size() > 0 && concepts.contains(regimenObsList.get(0).getValueCoded())) {
                stabilityCriteria.setOnThirdRegimen(true);
            } else {
                stabilityCriteria.setOnThirdRegimen(false);
            }

            /**
             * Clinic Staging
             */

            List<Concept> clinicStage = new ArrayList<>();
            List<Obs> clinicStageObsList = getObsListFromIdList("SELECT obs_id FROM obs where obs.person_id='" + patient.getPatientId() + "' AND obs.concept_id IN (99083,90203) AND obs.voided = false  ORDER BY  obs.encounter_id DESC");

            if (clinicStageObsList.size() > 0) {
                stabilityCriteria.setConceptForClinicStage(clinicStageObsList.get(0).getValueCoded().getConceptId());

            } else {
                stabilityCriteria.setConceptForClinicStage(null);
            }

            /**
             * Sputum Results
             */
            Obs sputumResults = getMostRecentObservation(encounterVisit, "307");
            if (sputumResults != null) {
                stabilityCriteria.setSputumResultObs(sputumResults);
                stabilityCriteria.setSputumResultObsValueConceptId(sputumResults.getValueCoded().getConceptId());
            }


            /**
             * Sputum ResultDate
             */
            stabilityCriteria.setSputumResultDateObs(getMostRecentObservation(encounterVisit, "99392"));


            /**
             * Current Regimen
             */
            stabilityCriteria.setArtStartDate(getArtStartDate(patient));
            stabilityCriteria.setEnableCliniciansMakeStabilityDecisions(allowCliniciansToMakeDecisionOnDSDM);
        } else {
            stabilityCriteria.setEnableCliniciansMakeStabilityDecisions(allowCliniciansToMakeDecisionOnDSDM);
        }
        return stabilityCriteria;
    }


    /**
     * This Subtracts a date provided by number of moths and years given and returns a new date
     *
     * @param referenceDate
     * @param noOfMoths
     * @return
     */
    public String getDateBefore(Date referenceDate, int noOfMoths, int noOfYears) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(referenceDate);
        if (noOfMoths != 0) {
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        }
        cal.add(Calendar.MONTH, noOfMoths);
        cal.add(Calendar.YEAR, noOfYears);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String finalDate = null;
        try {
            finalDate = format.format(cal.getTime());
        } catch (Exception e) {
            log.error("Failed to format date", e);
        }
        return finalDate;
    }

    /**
     * Gets List of Obs Basing on the query provided
     *
     * @param query
     * @return
     */
    private List<Obs> getObsListFromIdList(String query) {
        List<Obs> obsList = new ArrayList<>();
        for (Object o : Context.getAdministrationService().executeSQL(query, true)) {
            obsList.add(Context.getObsService().getObs(Integer.parseInt(((ArrayList) o).get(0).toString())));
        }
        return obsList;
    }

    /**
     * Get ART START DATE From Summary Page of a Patient
     *
     * @param patient
     * @return
     */
    public Date getArtStartDate(Patient patient) {
        List<Obs> list = Context.getObsService().getObservationsByPersonAndConcept(patient, Context.getConceptService().getConcept(99161));
        Date artStartDate = null;
        if (list.size() > 0) {
            artStartDate = list.get(0).getValueDatetime();
        }
        return artStartDate;

    }

    /**
     * Check if DTG
     *
     * @param obs
     * @return
     */
    private boolean checkIfDTG(Obs obs) {
        return "164976,164977,164978,164979".contains(obs.getValueCoded().getConceptId().toString());
    }


    /**
     * gets the latest Observation basing on the visit Date and the concepts
     *
     * @param encounterVisit
     * @param concepts       Separate with , if many
     * @return
     */
    public Obs getMostRecentObservation(Visit encounterVisit, String concepts) {
        String query = "SELECT obs_id FROM obs where  obs.obs_datetime <= DATE('" + encounterVisit.getStartDatetime() + "') AND obs.person_id='" + encounterVisit.getPatient().getPatientId() + "' AND concept_id in (" + concepts + ") AND obs.voided = false ORDER BY  obs.obs_datetime DESC LIMIT 0,1";
        List<Obs> obs = getObsListFromIdList(query);
        if (!obs.isEmpty()) {
            return obs.get(0);
        }
        return null;
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
