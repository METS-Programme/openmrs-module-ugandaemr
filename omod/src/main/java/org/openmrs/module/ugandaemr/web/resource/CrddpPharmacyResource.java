package org.openmrs.module.ugandaemr.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.UgandaEMRConstants;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/" + UgandaEMRConstants.MODULE_ID)
public class CrddpPharmacyResource {

    @GetMapping("/crddpPharmacies")
    public List<Map<String, Object>> getCrddpPharmacies(
            @RequestParam("cohortTypeUuid") String cohortTypeUuid) {

        return Context.getService(UgandaEMRService.class).getCrddpPharmacies(cohortTypeUuid);
    }
}