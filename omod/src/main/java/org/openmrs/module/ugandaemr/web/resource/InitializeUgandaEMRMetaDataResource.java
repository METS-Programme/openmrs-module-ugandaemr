package org.openmrs.module.ugandaemr.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.UgandaEMRConstants;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InitializeUgandaEMRMetaDataResource {

    @RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/" + UgandaEMRConstants.UGANDAEMR_MODULE_ID
            + "/initializemetadata", method = RequestMethod.GET)

    public ResponseEntity<Object> evaluate() {
        UgandaEMRService ugandaEMRService=Context.getService(UgandaEMRService.class);
        Map map=ugandaEMRService.initializeMetaData();

        if (!map.isEmpty()) {
            return ResponseEntity.ok().body(map);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
