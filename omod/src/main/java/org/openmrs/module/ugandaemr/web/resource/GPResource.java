package org.openmrs.module.ugandaemr.web.resource;

import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.ugandaemr.UgandaEMRConstants;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GPResource {

    @RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/" + UgandaEMRConstants.UGANDAEMR_MODULE_ID
            + "/gp", method = RequestMethod.GET)


    public ResponseEntity<Map<String, Object>> evaluate(
            @RequestParam(value = "property", required = true) String propertyName) {

        String userPrivilege = "Get Global Properties";
        try {
            Context.addProxyPrivilege(userPrivilege);

            String healthCenterName =
                    Context.getAdministrationService().getGlobalProperty(propertyName);

            if (healthCenterName == null || healthCenterName.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("property", propertyName);
            response.put("value", healthCenterName);

            return ResponseEntity.ok(response);

        } finally {
            Context.removeProxyPrivilege(userPrivilege);
        }
    }

}
