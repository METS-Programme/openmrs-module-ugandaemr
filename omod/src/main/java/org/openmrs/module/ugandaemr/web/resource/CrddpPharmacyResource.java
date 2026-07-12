package org.openmrs.module.ugandaemr.web.resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.UgandaEMRConstants;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
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

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/" + UgandaEMRConstants.MODULE_ID)
public class CrddpPharmacyResource extends BaseRestController {

	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET, value = "/crddpPharmacies")
	@ResponseBody
	public Object getCrddpPharmacies(@RequestParam("cohortTypeUuid") String cohortTypeUuid) {
		if (StringUtils.isBlank(cohortTypeUuid)) {
			return new ResponseEntity<Object>("You must specify cohortTypeUuid in the request!",
					new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}

		return Context.getService(UgandaEMRService.class).getCrddpPharmacies(cohortTypeUuid);
	}
}