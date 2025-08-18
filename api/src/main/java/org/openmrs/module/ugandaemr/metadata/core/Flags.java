package org.openmrs.module.ugandaemr.metadata.core;

import org.openmrs.module.patientflags.metadatadeploy.descriptor.FlagDescriptor;

import java.util.Arrays;
import java.util.List;

public class Flags {

    public static FlagDescriptor DUE_FOR_FIRST_VIRAL_LOAD = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(baseline_regimen_start_date, INTERVAL 6 MONTH), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_audit_tool_art_patients mfatap on p.patient_id = mfatap.client_id\n" +
                    "LEFT JOIN mamba_fact_patients_latest_viral_load mfplvl ON mfatap.client_id = mfplvl.client_id\n" +
                    "LEFT JOIN mamba_fact_transfer_out mfto ON mfatap.client_id = mfto.client_id\n" +
                    "LEFT JOIN mamba_fact_patients_latest_viral_load_ordered mfplvlo ON mfatap.client_id = mfplvlo.client_id\n" +
                    "WHERE (CURRENT_DATE() BETWEEN DATE_ADD(baseline_regimen_start_date, INTERVAL 5 MONTH) AND DATE_ADD(baseline_regimen_start_date, INTERVAL 6 MONTH))\n" +
                    "AND dead=false\n" +
                    "AND mfplvl.client_id is NULL\n" +
                    "AND mfto.client_id is NULL\n" +
                    "AND mfplvlo.client_id is NULL";
        }

        @Override
        public String message() {
            return "Due for 1st Viral on ${1}";
        }

        @Override
        public String priority() {
            return Priorites.GREEN.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Due for 1st Viral Load";
        }

        @Override
        public String description() {
            return "Patients who are due for their first viral load after enrollment into HIV Care";
        }

        @Override
        public String uuid() {
            return "7376f82e-225c-4340-9a8d-22e679532f37";
        }
    };

    public static FlagDescriptor OVERDUE_FOR_FIRST_VIRAL_LOAD = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(baseline_regimen_start_date, INTERVAL 6 MONTH), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_audit_tool_art_patients mfatap on p.patient_id = mfatap.client_id\n" +
                    "LEFT JOIN mamba_fact_patients_latest_viral_load mfplvl ON mfatap.client_id = mfplvl.client_id\n" +
                    "LEFT JOIN mamba_fact_transfer_out mfto ON mfatap.client_id = mfto.client_id\n" +
                    "LEFT JOIN mamba_fact_patients_latest_viral_load_ordered mfplvlo ON mfatap.client_id = mfplvlo.client_id\n" +
                    "WHERE CURRENT_DATE() >= DATE_ADD(baseline_regimen_start_date, INTERVAL 6 MONTH)\n" +
                    "AND dead=false\n" +
                    "AND mfplvl.client_id is NULL\n" +
                    "AND mfto.client_id is NULL\n" +
                    "AND mfplvlo.client_id is NULL\n" +
                    "AND TIMESTAMPDIFF(YEAR ,baseline_regimen_start_date,CURDATE())< 1;";
        }

        @Override
        public String message() {
            return "First Viral Load Overdue from ${1}";
        }

        @Override
        public String priority() {
            return Priorites.RED.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Overdue for First Viral Load";
        }

        @Override
        public String description() {
            return "Patients who are overdue for their first viral load after enrollment into HIV Care";
        }

        @Override
        public String uuid() {
            return "6ce583d1-a4d7-41a6-902f-9a5debea1ec7";
        }
    };

    public static FlagDescriptor BLED_FOR_VIRAL_LOAD = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id,DATE_FORMAT(order_date,'%d.%b.%Y')\n" +
                    "FROM patient p inner join  mamba_fact_patients_latest_viral_load_ordered mfplvlo on p.patient_id=mfplvlo.client_id\n" +
                    "         INNER JOIN mamba_fact_art_patients mfap ON mfplvlo.client_id = mfap.client_id\n" +
                    "        WHERE dead= FALSE and TIMESTAMPDIFF(MONTH,order_date,CURDATE()) <=1";
        }

        @Override
        public String message() {
            return "Patient was bled for viral load on ${1} awaiting results";
        }

        @Override
        public String priority() {
            return Priorites.GREEN.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Bled for Viral load";
        }

        @Override
        public String description() {
            return "Patients who have been bled for viral load and awaiting results";
        }

        @Override
        public String uuid() {
            return "424cda04-c1ec-4c57-99ca-6c443d48fe6b";
        }
    };

    public static FlagDescriptor DUE_FOR_ROUTINE_VIRAL_LOAD = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id,\n" +
                    "       DATE_FORMAT(\n" +
                    "               IF(TIMESTAMPDIFF(YEAR, birthdate, CURDATE()) < 16,\n" +
                    "                  DATE_ADD(mfplvl.hiv_viral_collection_date, INTERVAL 6 MONTH),\n" +
                    "                  DATE_ADD(mfplvl.hiv_viral_collection_date, INTERVAL 12 MONTH)), '%d.%b.%Y')\n" +
                    "FROM patient p inner join  mamba_fact_patients_latest_viral_load mfplvl on p.patient_id = mfplvl.client_id\n" +
                    "         INNER JOIN mamba_fact_art_patients mfap ON mfplvl.client_id = mfap.client_id\n" +
                    "         LEFT JOIN mamba_fact_transfer_out mfto ON mfplvl.client_id = mfto.client_id\n" +
                    "         LEFT JOIN mamba_fact_transfer_in mfti ON mfti.client_id = mfto.client_id\n" +
                    "         LEFT JOIN mamba_fact_patients_latest_viral_load_ordered mfplvlo ON mfplvl.client_id = mfplvlo.client_id\n" +
                    "WHERE mfap.dead = FALSE\n" +
                    "  AND (mfti.client_id IS NULL OR mfti.transfer_in_date > mfto.transfer_out_date)\n" +
                    "  AND (mfplvlo.client_id IS NULL OR TIMESTAMPDIFF(MONTH,order_date,CURDATE()) >=2)\n" +
                    "  AND DATEDIFF(IF(TIMESTAMPDIFF(YEAR, birthdate, CURDATE()) < 16, DATE_ADD(hiv_viral_collection_date, INTERVAL 6 MONTH),\n" +
                    "         DATE_ADD(hiv_viral_collection_date, INTERVAL 12 MONTH)),CURDATE()) BETWEEN 0 AND 30;";
        }

        @Override
        public String message() {
            return "Due for Routine Viral Load on ${1}";
        }

        @Override
        public String priority() {
            return Priorites.GREEN.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Due for Routine Viral Load";
        }

        @Override
        public String description() {
            return "Patients who are due for their routine viral load";
        }

        @Override
        public String uuid() {
            return "5bc66261-7ad6-4c66-97d9-eb3c511a9274";
        }
    };

    public static FlagDescriptor OVERDUE_FOR_ROUTINE_VIRAL_LOAD = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id,\n" +
                    "       DATE_FORMAT(\n" +
                    "               IF(TIMESTAMPDIFF(YEAR, birthdate, CURDATE()) < 16,\n" +
                    "                  DATE_ADD(mfplvl.hiv_viral_collection_date, INTERVAL 6 MONTH),\n" +
                    "                  DATE_ADD(mfplvl.hiv_viral_collection_date, INTERVAL 12 MONTH)), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_patients_latest_viral_load mfplvl on p.patient_id =mfplvl.client_id\n" +
                    "         INNER JOIN mamba_fact_art_patients mfap ON mfplvl.client_id = mfap.client_id\n" +
                    "         LEFT JOIN mamba_fact_transfer_out mfto ON mfplvl.client_id = mfto.client_id\n" +
                    "         LEFT JOIN mamba_fact_transfer_in mfti ON mfti.client_id = mfto.client_id\n" +
                    "         LEFT JOIN mamba_fact_patients_latest_viral_load_ordered mfplvlo ON mfplvl.client_id = mfplvlo.client_id\n" +
                    "WHERE mfap.dead = FALSE AND mfplvl.hiv_viral_collection_date IS NOT NULL \n" +
                    "  AND (mfti.client_id IS NULL OR mfti.transfer_in_date > mfto.transfer_out_date)\n" +
                    "  AND (mfplvlo.client_id IS NULL OR TIMESTAMPDIFF(MONTH,order_date,CURDATE()) >=2)\n" +
                    "  AND CURRENT_DATE() >IF(TIMESTAMPDIFF(YEAR, birthdate, CURDATE()) < 16, DATE_ADD(hiv_viral_collection_date, INTERVAL 6 MONTH),\n" +
                    "         DATE_ADD(hiv_viral_collection_date, INTERVAL 12 MONTH));";
        }

        @Override
        public String message() {
            return "Overdue for Routine Viral Load from ${1}";
        }

        @Override
        public String priority() {
            return Priorites.RED.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Overdue for Routine Viral Load";
        }

        @Override
        public String description() {
            return "Patients who are overdue for their routine viral load";
        }

        @Override
        public String uuid() {
            return "ffa7a845-bcdd-4727-a68a-3bad8c09692d";
        }
    };

    public static FlagDescriptor UPCOMING_APPOINTMENT = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(latest_return_date, '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_active_in_care mfac on p.patient_id = mfac.client_id\n" +
                    "WHERE dead = FALSE\n" +
                    "  AND transfer_out_date IS NULL\n" +
                    "  AND latest_return_date >= CURRENT_DATE()";
        }

        @Override
        public String message() {
            return "Upcoming appointment on ${1}";
        }

        @Override
        public String priority() {
            return Priorites.GREEN.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Upcoming appointment";
        }

        @Override
        public String description() {
            return "Patients who have an upcoming appointment";
        }

        @Override
        public String uuid() {
            return "1cbc86cf-8a5a-4402-b56a-6489aa4d4f2d";
        }
    };

    public static FlagDescriptor MISSED_APPOINTMENT = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(latest_return_date, '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_active_in_care mfac on p.patient_id = mfac.client_id\n" +
                    "WHERE dead = FALSE\n" +
                    "AND days_left_to_be_lost BETWEEN 7 AND 27\n" +
                    "  AND transfer_out_date IS NULL;";
        }

        @Override
        public String message() {
            return "Missed appointment on ${1}  ";
        }

        @Override
        public String priority() {
            return Priorites.ORANGE.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Missed appointment";
        }

        @Override
        public String description() {
            return "Patients who have missed appointment - this is between 7 to 29 days";
        }

        @Override
        public String uuid() {
            return "a248d8ca-9e60-4a54-a417-fcf00302fdb2";
        }
    };

    public static FlagDescriptor PATIENT_LOST = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(latest_return_date, INTERVAL 28 DAY), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_active_in_care mfac on mfac.client_id = p.patient_id\n" +
                    "WHERE dead = FALSE\n" +
                    "  AND days_left_to_be_lost  BETWEEN 28 AND 89\n" +
                    "  AND transfer_out_date IS NULL;";
        }

        @Override
        public String message() {
            return "Lost since ${1}";
        }

        @Override
        public String priority() {
            return Priorites.RED.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Lost";
        }

        @Override
        public String description() {
            return "Patients who have missed appointment - this is between 30 to 89 days";
        }

        @Override
        public String uuid() {
            return "4d10da66-1ede-4a92-aa71-a8dedb13a0ba";
        }
    };

    public static FlagDescriptor PATIENT_LOST_TO_FOLLOWUP = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(latest_return_date, INTERVAL 90 DAY), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_active_in_care mfac on p.patient_id = mfac.client_id\n" +
                    "WHERE dead = FALSE\n" +
                    "  AND days_left_to_be_lost >= 90\n" +
                    "  AND transfer_out_date IS NULL;";
        }

        @Override
        public String message() {
            return "Lost to follow-up since ${1}";
        }

        @Override
        public String priority() {
            return Priorites.RED.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Lost to Followup ";
        }

        @Override
        public String description() {
            return "Patients who have spent more than 90 days since their expected return date";
        }

        @Override
        public String uuid() {
            return "5a1f8283-9d5a-4efe-89a3-5634e01c8083";
        }
    };

    public static FlagDescriptor DUE_FOR_FIRST_DNA_PCR = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(eid_dob, INTERVAL 6 WEEK), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_eid_patients mfep on p.patient_id = mfep.client_id\n" +
                    "         INNER JOIN mamba_dim_person mdp ON mfep.client_id = mdp.person_id\n" +
                    "        LEFT JOIN mamba_fact_art_patients mfap ON mfep.client_id = mfap.client_id\n" +
                    "        LEFT JOIN mamba_fact_transfer_out mfto on mfto.client_id = mfep.client_id\n" +
                    "WHERE mdp.dead = FALSE\n" +
                    "  AND (TIMESTAMPDIFF(WEEK, eid_dob, CURDATE()) BETWEEN 6 AND 9)\n" +
                    "AND mfap.client_id is NULL\n" +
                    "AND `1ST_PCR_DATE` is NULL\n" +
                    "AND mfto.client_id is NULL;";
        }

        @Override
        public String message() {
            return "Due for 1st DNA PCR on ${1}";
        }

        @Override
        public String priority() {
            return Priorites.GREEN.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Due for 1st DNA PCR";
        }

        @Override
        public String description() {
            return "Exposed infants who are due for their first DNA PCR, between 6 to 9 weeks";
        }

        @Override
        public String uuid() {
            return "e8141fdf-4359-461d-840a-216442d45392";
        }
    };

    public static FlagDescriptor OVERDUE_FOR_FIRST_DNA_PCR = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(eid_dob, INTERVAL 6 WEEK), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_eid_patients mfep on p.patient_id = mfep.client_id\n" +
                    "         INNER JOIN mamba_dim_person mdp ON mfep.client_id = mdp.person_id\n" +
                    "        LEFT JOIN mamba_fact_art_patients mfap ON mfep.client_id = mfap.client_id\n" +
                    "        LEFT JOIN mamba_fact_transfer_out mfto on mfto.client_id = mfep.client_id\n" +
                    "WHERE mdp.dead = FALSE\n" +
                    "  AND (TIMESTAMPDIFF(WEEK, eid_dob, CURDATE()) BETWEEN 10 AND 24)\n" +
                    "AND mfap.client_id is NULL\n" +
                    "AND `1ST_PCR_DATE` is NULL\n" +
                    "AND mfto.client_id is NULL;";
        }

        @Override
        public String message() {
            return "Overdue for 1st DNA PCR from ${1}";
        }

        @Override
        public String priority() {
            return Priorites.RED.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Overdue for 1st DNA PCR";
        }

        @Override
        public String description() {
            return "Exposed infants who are overdue for their first DNA PCR who are aged between 10 to 24 weeks";
        }

        @Override
        public String uuid() {
            return "162e4459-24b9-4a0e-ae10-08413d48aecb";
        }
    };

    public static FlagDescriptor DUE_FOR_SECOND_DNA_PCR = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(eid_dob, INTERVAL 9 WEEK), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_eid_patients mfep on p.patient_id = mfep.client_id\n" +
                    "         INNER JOIN mamba_dim_person mdp ON mfep.client_id = mdp.person_id\n" +
                    "        LEFT JOIN mamba_fact_art_patients mfap ON mfep.client_id = mfap.client_id\n" +
                    "        LEFT JOIN mamba_fact_transfer_out mfto on mfto.client_id = mfep.client_id\n" +
                    "WHERE mdp.dead = FALSE\n" +
                    "  AND (TIMESTAMPDIFF(WEEK, eid_dob, CURDATE()) BETWEEN 9 AND 10)\n" +
                    "AND mfap.client_id is NULL\n" +
                    "AND `2ND_PCR_DATE` is NULL\n" +
                    "AND mfto.client_id is NULL;";
        }

        @Override
        public String message() {
            return "Due for 2nd DNA PCR on ${1}";
        }

        @Override
        public String priority() {
            return Priorites.GREEN.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Due for 2nd DNA PCR";
        }

        @Override
        public String description() {
            return "Exposed infants who are due for their second DNA PCR aged between 13 and 14 months";
        }

        @Override
        public String uuid() {
            return "4e0527ed-1a5f-4c83-b372-031829eb334b";
        }
    };

    public static FlagDescriptor OVERDUE_FOR_SECOND_DNA_PCR = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(eid_dob, INTERVAL 9 WEEK), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_eid_patients mfep on p.patient_id = mfep.client_id\n" +
                    "         INNER JOIN mamba_dim_person mdp ON mfep.client_id = mdp.person_id\n" +
                    "        LEFT JOIN mamba_fact_art_patients mfap ON mfep.client_id = mfap.client_id\n" +
                    "        LEFT JOIN mamba_fact_transfer_out mfto on mfto.client_id = mfep.client_id\n" +
                    "WHERE mdp.dead = FALSE\n" +
                    "  AND (TIMESTAMPDIFF(WEEK, eid_dob, CURDATE()) BETWEEN 11 AND 13)\n" +
                    "AND mfap.client_id is NULL\n" +
                    "AND `2ND_PCR_DATE` is NULL\n" +
                    "AND mfto.client_id is NULL;";
        }

        @Override
        public String message() {
            return "Overdue for 2nd DNA PCR from ${1}";
        }

        @Override
        public String priority() {
            return Priorites.RED.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Overue for 2nd DNA PCR";
        }

        @Override
        public String description() {
            return "Exposed infants who are overdue for their second DNA PCR who are between 15 and 17 months since Rapid Test is due at 18 months";
        }

        @Override
        public String uuid() {
            return "356357b4-c285-4a92-82b2-ddcfe6cdf3f4";
        }
    };

    public static FlagDescriptor DUE_FOR_THIRD_DNA_PCR = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(eid_dob, INTERVAL 13 MONTH), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_eid_patients mfep on p.patient_id = mfep.client_id\n" +
                    "         INNER JOIN mamba_dim_person mdp ON mfep.client_id = mdp.person_id\n" +
                    "        LEFT JOIN mamba_fact_art_patients mfap ON mfep.client_id = mfap.client_id\n" +
                    "        LEFT JOIN mamba_fact_transfer_out mfto on mfto.client_id = mfep.client_id\n" +
                    "WHERE mdp.dead = FALSE\n" +
                    "  AND (TIMESTAMPDIFF(MONTH, eid_dob, CURDATE()) BETWEEN 13 AND 14)\n" +
                    "AND mfap.client_id is NULL\n" +
                    "AND repeat_pcr_date is NULL\n" +
                    "AND mfto.client_id is NULL;";
        }

        @Override
        public String message() {
            return "Due for 3rd DNA PCR on ${1}";
        }

        @Override
        public String priority() {
            return Priorites.GREEN.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Due for 3rd DNA PCR";
        }

        @Override
        public String description() {
            return "Exposed infants who are due for their third DNA PCR aged between 13 and 14 months";
        }

        @Override
        public String uuid() {
            return "636a9bd2-9f13-45ca-886b-9380a959965";
        }
    };


    public static FlagDescriptor OVERDUE_FOR_THIRD_DNA_PCR = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(eid_dob, INTERVAL 13 MONTH), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_eid_patients mfep on p.patient_id =mfep.client_id\n" +
                    "         INNER JOIN mamba_dim_person mdp ON mfep.client_id = mdp.person_id\n" +
                    "        LEFT JOIN mamba_fact_art_patients mfap ON mfep.client_id = mfap.client_id\n" +
                    "        LEFT JOIN mamba_fact_transfer_out mfto on mfto.client_id = mfep.client_id\n" +
                    "WHERE mdp.dead = FALSE\n" +
                    "  AND (TIMESTAMPDIFF(MONTH, eid_dob, CURDATE()) BETWEEN 15 AND 17)\n" +
                    "AND mfap.client_id is NULL\n" +
                    "AND repeat_pcr_date is NULL\n" +
                    "AND mfto.client_id is NULL;";
        }

        @Override
        public String message() {
            return "OverDue for 3rd DNA PCR on ${1}";
        }

        @Override
        public String priority() {
            return Priorites.RED.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "OverDue for 3rd DNA PCR";
        }

        @Override
        public String description() {
            return "Exposed infants who are overdue for their third DNA PCR, 6 weeks after breastfeeding and having taken a 2nd DNA PCR";
        }

        @Override
        public String uuid() {
            return "65f4da17-5fa3-497e-848f-cc515b69ff81";
        }
    };

    public static FlagDescriptor DUE_FOR_RAPID_TEST = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(eid_dob, INTERVAL 18 MONTH), '%d.%b.%Y')\n" +
                    "FROM patient p inner join mamba_fact_eid_patients mfep on p.patient_id = mfep.client_id\n" +
                    "         INNER JOIN mamba_dim_person mdp ON mfep.client_id = mdp.person_id\n" +
                    "        LEFT JOIN mamba_fact_art_patients mfap ON mfep.client_id = mfap.client_id\n" +
                    "        LEFT JOIN mamba_fact_transfer_out mfto on mfto.client_id = mfep.client_id\n" +
                    "WHERE mdp.dead = FALSE\n" +
                    "  AND (TIMESTAMPDIFF(MONTH, eid_dob, CURDATE()) BETWEEN 18 AND 19)\n" +
                    "AND mfap.client_id is NULL\n" +
                    "AND rapid_pcr_date is NULL\n" +
                    "AND mfto.client_id is NULL;";
        }

        @Override
        public String message() {
            return "Due for Rapid Test from ${1}";
        }

        @Override
        public String priority() {
            return Priorites.GREEN.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Due for Rapid Test";
        }

        @Override
        public String description() {
            return "Exposed infants who are due for an 18 month rapid test";
        }

        @Override
        public String uuid() {
            return "f3fb9704-5460-4041-9e77-6d5e9e34c202";
        }
    };

    public static FlagDescriptor OVERDUE_FOR_RAPID_TEST = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, DATE_FORMAT(DATE_ADD(eid_dob, INTERVAL 18 MONTH), '%d.%b.%Y')\n" +
                    "FROM patient p inner join  mamba_fact_eid_patients mfep on p.patient_id = mfep.client_id\n" +
                    "         INNER JOIN mamba_dim_person mdp ON mfep.client_id = mdp.person_id\n" +
                    "        LEFT JOIN mamba_fact_art_patients mfap ON mfep.client_id = mfap.client_id\n" +
                    "        LEFT JOIN mamba_fact_transfer_out mfto on mfto.client_id = mfep.client_id\n" +
                    "WHERE mdp.dead = FALSE\n" +
                    "  AND (TIMESTAMPDIFF(MONTH, eid_dob, CURDATE()) BETWEEN 20 AND 24)\n" +
                    "AND mfap.client_id is NULL\n" +
                    "AND rapid_pcr_date is NULL\n" +
                    "AND mfto.client_id is NULL; ";
        }

        @Override
        public String message() {
            return "Overdue for Rapid Test from ${1}";
        }

        @Override
        public String priority() {
            return Priorites.GREEN.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Overdue for Rapid Test";
        }

        @Override
        public String description() {
            return "Exposed infants who are overdue for an 18 month rapid test but not older than 24 months by which a final outcome is expected";
        }

        @Override
        public String uuid() {
            return "d144893e-b17b-4b77-916e-d3d2b3733378";
        }
    };

    public static FlagDescriptor HAS_DETECTABLE_VIRAL_LOAD = new FlagDescriptor()   {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, hiv_viral_load_copies, DATE_FORMAT(mfplvl.encounter_date, '%d. %b. %Y'), specimen_type\n" +
                    "FROM patient p inner join mamba_fact_patients_latest_viral_load mfplvl on p.patient_id = mfplvl.client_id\n" +
                    "         INNER JOIN mamba_fact_art_patients mfap ON mfplvl.client_id = mfap.client_id\n" +
                    "         LEFT JOIN mamba_fact_transfer_out mfto ON mfto.client_id = mfplvl.client_id\n" +
                    "WHERE (hiv_viral_load_copies>=1000) OR (specimen_type='WHOLE BLOOD' and hiv_viral_load_copies >200) OR (specimen_type='Dried Blood Spot' and hiv_viral_load_copies >400)\n" +
                    "    AND  dead = FALSE\n" +
                    "  AND mfto.client_id IS NULL;";
        }

        @Override
        public String message() {
            return "Detectable Viral Load of ${1} from ${2} may be due for IAC";
        }

        @Override
        public String priority() {
            return Priorites.RED.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Un-supressed Viral Load";
        }

        @Override
        public String description() {
            return "Patients who are have un-supressed viral load";
        }

        @Override
        public String uuid() {
            return "151801e2-0742-457f-8610-95530a4db232";
        }
    };

    public static FlagDescriptor PATIENT_TRANSFERED_OUT = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT p.patient_id, transfer_out_date\n" +
                    "FROM patient p inner join mamba_fact_transfer_out mfto on p.patient_id =mfto.client_id\n" +
                    "         INNER JOIN mamba_fact_art_patients mfap ON mfap.client_id = mfto.client_id\n" +
                    "         LEFT JOIN mamba_fact_transfer_in mfti on mfti.client_id = mfto.client_id\n" +
                    "where dead=false and (mfti.client_id is NULL or mfti.transfer_in_date > mfto.transfer_out_date)";
        }

        @Override
        public String message() {
            return "Patient Transfered Out on ${1} ";
        }

        @Override
        public String priority() {
            return Priorites.RED.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Transfered Out Patient";
        }

        @Override
        public String description() {
            return "Patients who are transfered Out to Another Facility";
        }

        @Override
        public String uuid() {
            return "c5cae7e7-d6e3-4d5f-b684-ea888b5a8a7c";
        }
    };

    public static FlagDescriptor SUSPECTED_MPOX_PATIENT = new FlagDescriptor() {
        @Override
        public String criteria() {
            return "SELECT DISTINCT\n" +
                    "    p.patient_id,\n" +
                    "    obs.value_coded,\n" +
                    "    CASE\n" +
                    "        WHEN obs.value_coded = 166313 THEN 'Transferred to Isolation Unit'\n" +
                    "        WHEN obs.value_coded = 168758 THEN 'Referred to Isolation Unit'\n" +
                    "        ELSE cn.name\n" +
                    "    END AS value_coded_text,\n" +
                    "    DATE_FORMAT(obs.date_created,'%d. %b. %Y') AS formatted_date\n" +
                    "FROM obs\n" +
                    "INNER JOIN patient p ON p.patient_id = obs.person_id\n" +
                    "INNER JOIN concept_name cn ON obs.value_coded = cn.concept_id\n" +
                    "WHERE obs.concept_id = 198959";
        }

        @Override
        public String message() {
            return "MPOX Suspect ${2} on ${3}";
        }

        @Override
        public String priority() {
            return Priorites.RED.uuid();
        }

        @Override
        public List<String> tags() {
            return Arrays.asList(Tags.PATIENT_STATUS.uuid());
        }

        @Override
        public String name() {
            return "Monkey POX Patient";
        }

        @Override
        public String description() {
            return "Monkey POX Patient";
        }

        @Override
        public String uuid() {
            return "8311e3eb-d87d-40f2-a35f-dd60f1782ddd";
        }
    };
}
