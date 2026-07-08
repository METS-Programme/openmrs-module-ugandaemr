package org.openmrs.module.ugandaemr.web.resource;

import org.hibernate.SessionFactory;
import org.openmrs.module.ugandaemr.UgandaEMRConstants;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/" + UgandaEMRConstants.MODULE_ID)
public class CrddpPharmacyResource {

    private final SessionFactory sessionFactory;

    @Autowired
    public CrddpPharmacyResource(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @GetMapping("/crddpPharmacies")
    public List<Map<String, Object>> getCrddpPharmacies(
            @RequestParam("cohortTypeUuid") String cohortTypeUuid) {

        final String sql =
                "SELECT c.name AS name, c.uuid AS uuid " +
                        "FROM cohort c " +
                        "INNER JOIN cohort_type ct " +
                        "ON ct.cohort_type_id = c.cohort_type_id " +
                        "WHERE ct.uuid = :cohortTypeUuid " +
                        "AND c.voided = 0 " +
                        "AND ct.voided = 0 " +
                        "ORDER BY c.name";

        @SuppressWarnings("unchecked")
        List<Object[]> rows = sessionFactory
                .getCurrentSession()
                .createSQLQuery(sql)
                .setParameter("cohortTypeUuid", cohortTypeUuid)
                .list();

        List<Map<String, Object>> pharmacies = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> pharmacy = new LinkedHashMap<>();
            pharmacy.put("name", row[0]);
            pharmacy.put("uuid", row[1]);
            pharmacies.add(pharmacy);
        }

        return pharmacies;
    }
}