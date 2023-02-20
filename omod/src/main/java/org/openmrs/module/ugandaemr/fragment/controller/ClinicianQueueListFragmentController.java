package org.openmrs.module.ugandaemr.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.patientqueueing.api.PatientQueueingService;
import org.openmrs.module.patientqueueing.mapper.PatientQueueMapper;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.module.ugandaemr.api.PatientQueueVisitMapper;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ClinicianQueueListFragmentController {

    protected final Log log = LogFactory.getLog(getClass());

    public ClinicianQueueListFragmentController() {
        //Constructor
    }

    public void controller(@SpringBean FragmentModel pageModel, @SpringBean("locationService") LocationService locationService,UiSessionContext uiSessionContext) {
        List<String> list = new ArrayList();

        String locationUUIDS = Context.getAdministrationService()
                .getGlobalProperty("ugandaemr.clinicianLocationUUIDS");

        List clinicianLocationUUIDList = Arrays.asList(locationUUIDS.split(","));

        pageModel.put("clinicianLocationUUIDList", clinicianLocationUUIDList);

        pageModel.put("locationList", (locationService.getRootLocations(false).get(0)).getChildLocations());
        pageModel.put("clinicianLocation", clinicianLocationUUIDList);
        pageModel.put("currentProvider", uiSessionContext.getCurrentProvider());
        pageModel.put("enablePatientQueueSelection", Context.getAdministrationService().getGlobalProperty("ugandaemr.enablePatientQueueSelection"));
    }

    public SimpleObject getPatientQueueList(@RequestParam(value = "searchfilter", required = false) String searchfilter, UiSessionContext uiSessionContext) {
        UgandaEMRService ugandaEMRService = Context.getService(UgandaEMRService.class);
        PatientQueueingService patientQueueingService = Context.getService(PatientQueueingService.class);
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleObject simpleObject = new SimpleObject();

        List<PatientQueue> patientQueueList = new ArrayList();
        if (!searchfilter.equals("")) {
            patientQueueList = patientQueueingService.getPatientQueueListBySearchParams(searchfilter, OpenmrsUtil.firstSecondOfDay(new Date()), OpenmrsUtil.getLastMomentOfDay(new Date()), uiSessionContext.getSessionLocation(), null, null);
        } else {
            patientQueueList = patientQueueingService.getPatientQueueListBySearchParams(searchfilter, OpenmrsUtil.firstSecondOfDay(new Date()), OpenmrsUtil.getLastMomentOfDay(new Date()), uiSessionContext.getSessionLocation(), null, null);
        }
        List<PatientQueueVisitMapper> patientQueueMappers = ugandaEMRService.mapPatientQueueToMapper(patientQueueList);
        try {
            simpleObject.put("patientClinicianQueueList", objectMapper.writeValueAsString(patientQueueMappers));
        } catch (IOException e) {
            log.error(e);
        }
        return simpleObject;
    }
}
