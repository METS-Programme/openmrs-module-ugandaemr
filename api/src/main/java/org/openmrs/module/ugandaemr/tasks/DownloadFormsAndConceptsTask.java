package org.openmrs.module.ugandaemr.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.scheduler.tasks.AbstractTask;

public class DownloadFormsAndConceptsTask extends AbstractTask {
    protected final Log log = LogFactory.getLog(this.getClass());
    @Override
    public void execute() {
        Context.getService(UgandaEMRService.class).downloadFormsAndMetaDataFromGitHub();
    }
}
