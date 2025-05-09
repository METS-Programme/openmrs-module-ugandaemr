package org.openmrs.module.ugandaemr.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientqueueing.api.PatientQueueingService;
import org.openmrs.module.patientqueueing.model.PatientQueue;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StopActiveFacilityVisitTask extends AbstractTask {

    protected final Log log = LogFactory.getLog(this.getClass());
    private static final String FACILITY_VISIT_TYPE_UUID = "7b0f5697-27e3-40c4-8bae-f4049abfb4ed"; // UUID of the visit type
    private static final String TASK_NAME = "Close Active Facility Visits";

    @Override
    public void execute() {
        VisitService visitService = Context.getVisitService();
        PatientQueueingService patientQueueingService = Context.getService(PatientQueueingService.class);
        Date todayStart = OpenmrsUtil.firstSecondOfDay(new Date());

        try {
            // Fetch pending and picked queues
            List<PatientQueue> pendingQueues = patientQueueingService.getPatientQueueList(
                    null, null, todayStart, null, null, null, PatientQueue.Status.PENDING);
            List<PatientQueue> pickedQueues = patientQueueingService.getPatientQueueList(
                    null, null, todayStart, null, null, null, PatientQueue.Status.PICKED);

            // Merge queues and complete them
            List<PatientQueue> incompleteQueues = new ArrayList<>();
            incompleteQueues.addAll(pendingQueues);
            incompleteQueues.addAll(pickedQueues);

            for (PatientQueue patientQueue : incompleteQueues) {
                try {
                    patientQueueingService.completePatientQueue(patientQueue);
                    log.info("Completed queue for patient: " + patientQueue.getPatient().getId());
                } catch (Exception e) {
                    log.error("Error completing queue for patient: " + patientQueue.getPatient().getId(), e);
                }
            }

            // Process visits: Look for active visits that should be closed
            List<Visit> visitList = visitService.getAllVisits();
            Date currentDayStart = OpenmrsUtil.firstSecondOfDay(new Date());

            visitList.stream()
                    .filter(visit -> visit.getStopDatetime() == null // Visit is still active
                            && FACILITY_VISIT_TYPE_UUID.equals(visit.getVisitType().getUuid()) // It's a facility visit
                            && visit.getStartDatetime().before(currentDayStart)) // It started before today
                    .forEach(visit -> {
                        try {
                            Date latestEncounterDate = getLatestEncounterDate(visit);
                            visitService.endVisit(visit, latestEncounterDate); // End the visit at the latest date
                            log.info("Ended visit for patient: " + visit.getPatient().getId() + " at " + latestEncounterDate);
                        } catch (Exception e) {
                            log.error("Error ending visit for patient: " + visit.getPatient().getId(), e);
                        }
                    });

        } catch (Exception e) {
            log.error("Failed to execute StopActiveFacilityVisitTask", e);
        }
    }

    /**
     * Helper method to get the latest encounter date in the visit (or end of visit day).
     */
    private Date getLatestEncounterDate(Visit visit) {
        Date latestDate = OpenmrsUtil.getLastMomentOfDay(visit.getStartDatetime());

        for (Encounter encounter : visit.getEncounters()) {
            if (encounter.getEncounterDatetime().after(latestDate)) {
                latestDate = OpenmrsUtil.getLastMomentOfDay(encounter.getEncounterDatetime());
            }
        }
        return latestDate;
    }
}
