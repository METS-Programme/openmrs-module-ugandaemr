package org.openmrs.module.ugandaemr.web.resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.UgandaEMRConstants;
import org.openmrs.module.ugandaemr.api.SimpleObject;
import org.openmrs.module.ugandaemr.utils.EncounterBasedRegimenUtils;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1)
public class CareProgramResource extends BaseRestController {

    protected final Log log = LogFactory.getLog(getClass());
    static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");


    public static String HIV_PROGRAM_UUID = "dfdc6d40-2f2f-463d-ba90-cc97350441a8";
    public static String MCH_CHILD_PROGRAM_UUID = "c2ecdf11-97cd-432a-a971-cfd9bd296b83";
    public static String MCH_MOTHER_PROGRAM_UUID = "b5d9e05f-f5ab-4612-98dd-adb75438ed34";
    public static String TB_PROGRAM_UUID = "9f144a34-3a4a-44a9-8486-6b7af6cc64f6";
    public static String TPT_PROGRAM_UUID = "335517a1-04bc-438b-9843-1ba49fb7fcd9";
    public static String OVC_PROGRAM_UUID = "6eda83f0-09d9-11ea-8d71-362b9e155667";
    public static String OTZ_PROGRAM_UUID = "24d05d30-0488-11ea-8d71-362b9e155667";
    public static String VMMC_PROGRAM_UUID = "228538f4-cad9-476b-84c3-ab0086150bcc";
    public static String PREP_PROGRAM_UUID = "214cad1c-bb62-4d8e-b927-810a046daf62";
    public static String KP_PROGRAM_UUID = "7447305a-18a7-11e9-ab14-d663bd873d93";
    public static final String KP_CLIENT_ENROLMENT = "c7f47cea-207b-11e9-ab14-d663bd873d93";
    public static final String KP_CLIENT_DISCONTINUATION = "1f76643e-2495-11e9-ab14-d663bd873d93";

    public static final String PREP_ENROLLMENT_FORM = "d5ca78be-654e-4d23-836e-a934739be555";

    public static final String PREP_DISCONTINUATION_FORM = "467c4cc3-25eb-4330-9cf6-e41b9b14cc10";

    public static final String MCH_DELIVERY_FORM_UUID = "496c7cc3-0eea-4e84-a04c-2292949e2f7f";

    public static final String MCH_DISCHARGE_FORM_UUID = "af273344-a5f9-11e8-98d0-529269fb1459";
    public static final String LAB_ORDERS_FORM_UUID = "2cdeded1-3f69-3bda-beff-1ed2ead94eaf";

    public static final Locale LOCALE = Locale.ENGLISH;

    public String name = null;

    public static String ISONIAZID_DRUG_UUID = "78280AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static String RIFAMPIN_ISONIAZID_DRUG_UUID = "1194AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";



    /**
     * Fetches default facility
     *
     * @return custom location object
     */
    @RequestMapping(method = RequestMethod.GET, value = "/default-facility")
    @ResponseBody
    public Object getDefaultConfiguredFacility() {
        GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(UgandaEMRConstants.GP_DEFAULT_LOCATION);

        if (gp == null) {
            return new ResponseEntity<Object>("Default facility not configured!", new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        Location location = (Location) gp.getValue();
        ObjectNode locationNode = JsonNodeFactory.instance.objectNode();

        locationNode.put("locationId", location.getLocationId());
        locationNode.put("uuid", location.getUuid());
        locationNode.put("display", location.getName());

        return locationNode.toString();

    }

    /**
     * Returns regimen history for a patient
     * @param category // ARV or TB
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/regimenHistory")
    @ResponseBody
    public Object getRegimenHistory(@RequestParam("patientUuid") String patientUuid, @RequestParam("category") String category) {
        ObjectNode regimenObj = JsonNodeFactory.instance.objectNode();
        if (StringUtils.isBlank(patientUuid)) {
            return new ResponseEntity<Object>("You must specify patientUuid in the request!",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);

        if (patient == null) {
            return new ResponseEntity<Object>("The provided patient was not found in the system!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        ArrayNode regimenNode = JsonNodeFactory.instance.arrayNode();
        List<SimpleObject> obshistory = EncounterBasedRegimenUtils.getRegimenHistoryFromObservations(patient, category);
        for (SimpleObject obj : obshistory) {
            ObjectNode node = JsonNodeFactory.instance.objectNode();;
            node.put("startDate", obj.get("startDate").toString());
            node.put("endDate", obj.get("endDate").toString());
            node.put("regimenShortDisplay", obj.get("regimenShortDisplay").toString());
            node.put("regimenLine", obj.get("regimenLine").toString());
            node.put("regimenLongDisplay", obj.get("regimenLongDisplay").toString());
            node.put("changeReasons", obj.get("changeReasons").toString());
            node.put("regimenUuid", obj.get("regimenUuid").toString());
            node.put("current", obj.get("current").toString());
            regimenNode.add(node);
        }

        regimenObj.put("results", regimenNode);
        return regimenObj.toString();

    }

    /**
     * Gets a list of flags for a patient
     * @param request
     * @param patientUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/flags") // gets all flags for a patient
    @ResponseBody
    public Object getAllPatientFlags(HttpServletRequest request, @RequestParam("patientUuid") String patientUuid) {
        if (StringUtils.isBlank(patientUuid)) {
            return new ResponseEntity<Object>("You must specify patientUuid in the request!",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        ObjectNode flagsObj = JsonNodeFactory.instance.objectNode();

        if (patient == null) {
            return new ResponseEntity<Object>("The provided patient was not found in the system!",
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        ArrayNode flags = JsonNodeFactory.instance.arrayNode();
        flagsObj.put("results", flags);

        return flagsObj.toString();

    }















}
