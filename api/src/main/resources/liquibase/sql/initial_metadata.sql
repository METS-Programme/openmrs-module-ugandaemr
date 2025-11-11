-- MySQL dump 10.13  Distrib 8.0.41, for macos15.2 (arm64)
--
-- Host: localhost    Database: conceptreview
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `encounter_type`
--

DROP TABLE IF EXISTS `encounter_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `encounter_type` (
  `encounter_type_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `description` text,
  `creator` int NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL,
  `retired` tinyint(1) NOT NULL DEFAULT '0',
  `retired_by` int DEFAULT NULL,
  `date_retired` datetime DEFAULT NULL,
  `retire_reason` varchar(255) DEFAULT NULL,
  `uuid` char(38) NOT NULL,
  `edit_privilege` varchar(255) DEFAULT NULL,
  `view_privilege` varchar(255) DEFAULT NULL,
  `changed_by` int DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  PRIMARY KEY (`encounter_type_id`),
  UNIQUE KEY `encounter_type_name` (`name`),
  UNIQUE KEY `uuid_encounter_type` (`uuid`),
  KEY `encounter_type_changed_by` (`changed_by`),
  KEY `encounter_type_retired_status` (`retired`),
  KEY `privilege_which_can_edit_encounter_type` (`edit_privilege`),
  KEY `privilege_which_can_view_encounter_type` (`view_privilege`),
  KEY `user_who_created_type` (`creator`),
  KEY `user_who_retired_encounter_type` (`retired_by`),
  CONSTRAINT `encounter_type_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `privilege_which_can_edit_encounter_type` FOREIGN KEY (`edit_privilege`) REFERENCES `privilege` (`privilege`),
  CONSTRAINT `privilege_which_can_view_encounter_type` FOREIGN KEY (`view_privilege`) REFERENCES `privilege` (`privilege`),
  CONSTRAINT `user_who_created_type` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_who_retired_encounter_type` FOREIGN KEY (`retired_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `encounter_type`
--

LOCK TABLES `encounter_type` WRITE;
/*!40000 ALTER TABLE `encounter_type` DISABLE KEYS */;
INSERT INTO `encounter_type` VALUES (1,'Attachment Upload','Encounters used to record uploads of attachments.',2,'2024-07-24 11:11:42',0,NULL,NULL,NULL,'5021b1a1-e7f6-44b4-ba02-da2f2bcf8718',NULL,NULL,NULL,NULL),(2,'Discharge','Indicates that a patient\'s inpatient care at the hospital is ending, and they are expected to leave soon.',2,'2013-08-01 21:27:50',0,NULL,NULL,NULL,'181820aa-88c9-479b-9077-af92f5364329',NULL,NULL,2,'2025-06-03 10:38:32'),(3,'Admission','Indicates that the patient has been admitted for inpatient care, and is not expected to leave the hospital unless discharged.',2,'2013-08-01 21:27:27',0,NULL,NULL,NULL,'e22e39fd-7db2-45e7-80f1-60fa0d5a4378',NULL,NULL,2,'2025-06-03 10:38:32'),(4,'Visit Note','Encounter where a full or abbreviated examination is done, usually leading to a presumptive or confirmed diagnosis, recorded by the examining clinician.',2,'2013-08-02 08:16:06',0,NULL,NULL,NULL,'d7151f82-c1f3-4152-a605-2f9ea7414a79',NULL,NULL,2,'2025-06-03 10:38:32'),(5,'Check Out','The patient is explicitly leaving the hospital/clinic. (Usually no formal encounter is captured for this.)',2,'2013-08-02 08:14:43',0,NULL,NULL,NULL,'25a042b2-60bc-4940-a909-debd098b7d82',NULL,NULL,2,'2025-06-03 10:38:32'),(6,'Check In','Indicates the patient has done the required paperwork and check-in to begin a visit to the clinic/hospital.',2,'2013-08-02 08:13:23',0,NULL,NULL,NULL,'ca3aed11-1aa4-42a1-b85c-8332fc8001fc',NULL,NULL,2,'2025-06-03 10:38:32'),(7,'Transfer','Indicates that a patient is being transferred into a different department within the hospital. (Transfers out of the hospital should not use this encounter type.)',2,'2013-08-01 21:28:10',0,NULL,NULL,NULL,'7b68d557-85ef-4fc8-b767-4fa4f5eb5c23',NULL,NULL,2,'2025-06-03 10:38:32'),(8,'Vitals','For capturing vital signs',2,'2013-08-12 15:08:05',0,NULL,NULL,NULL,'67a71486-1a54-468f-ac3e-7091a9a79584',NULL,NULL,2,'2025-06-03 10:38:32'),(9,'ANC - Encounter','An encounter when a patient gets ANC services',2,'2016-04-05 16:43:20',0,NULL,NULL,NULL,'044daI6d-f80e-48fe-aba9-037f241905Pe',NULL,NULL,2,'2025-06-03 10:38:32'),(10,'LAB Encounter','Lab Encounter',2,'2016-04-05 16:43:20',0,NULL,NULL,NULL,'214e27a1-606a-4b1e-a96e-d736c87069d5',NULL,NULL,2,'2025-06-03 10:38:32'),(11,'SMC - Encounter','An encounter when a patient gets SMC services',2,'2016-04-05 16:43:20',0,NULL,NULL,NULL,'244da86d-f80e-48fe-aba9-067f241905ee',NULL,NULL,2,'2025-06-03 10:38:32'),(12,'HTC - Encounter','An encounter when a patient gets HCT services',2,'2016-04-05 16:43:20',0,NULL,NULL,NULL,'264daIZd-f80e-48fe-nba9-P37f2W1905Pv',NULL,NULL,2,'2025-06-03 10:38:32'),(13,'EID Card - Encounter','Outpatient Child Return Visit',2,'2016-04-05 16:43:20',0,NULL,NULL,NULL,'4345dacb-909d-429c-99aa-045f2db77e2b',NULL,NULL,2,'2025-06-03 10:38:32'),(14,'ART Card - Health Education Encounter','An Health education encounter created when a patient gets health education',2,'2016-04-05 16:43:20',0,NULL,NULL,NULL,'6d88e370-f2ba-476b-bf1b-d8eaf3b1b67e',NULL,NULL,2,'2025-06-03 10:38:32'),(15,'ART Card - Summary','Outpatient Adult Initial Visit',2,'2016-04-05 16:43:19',0,NULL,NULL,NULL,'8d5b27bc-c2cc-11de-8d13-0010c6dffd0f',NULL,NULL,2,'2025-06-03 10:38:32'),(16,'ART Card - Encounter','Outpatient Adult & Child Return Visit',2,'2016-04-05 16:43:20',0,NULL,NULL,NULL,'8d5b2be0-c2cc-11de-8d13-0010c6dffd0f',NULL,NULL,2,'2025-06-03 10:38:32'),(17,'EID Card - Summary','Outpatient Children Initial Visit',2,'2016-04-05 16:43:20',0,NULL,NULL,NULL,'9fcfcc91-ad60-4d84-9710-11cc25258719',NULL,NULL,2,'2025-06-03 10:38:32'),(18,'Maternity - Encounter','When a mother comes to a health facility to deliver',2,'2016-04-05 16:43:20',0,NULL,NULL,NULL,'a9f11592-22e7-45fc-904d-dfe24cb1fc67',NULL,NULL,2,'2025-06-03 10:38:32'),(19,'PNC - Encounter','An encounter when a patient gets PNC services',2,'2025-10-23 15:05:07',0,NULL,NULL,NULL,'fa6f3ff5-b784-43fb-ab35-a08ab7dbf074',NULL,NULL,2,'2025-10-23 15:05:07'),(20,'OPD Encounter','Outpatient Clinical Ecnounter',2,'2025-10-23 15:05:07',0,NULL,NULL,NULL,'ee4780f5-b5eb-423b-932f-00b5879df5ab',NULL,NULL,2,'2025-10-23 15:05:07'),(21,'TB Summary (Enrollment)','An encounter for the initial visit to the TB clinic',2,'2025-10-23 15:05:07',0,NULL,NULL,NULL,'334bf97e-28e2-4a27-8727-a5ce31c7cd66',NULL,NULL,2,'2025-10-23 15:05:07'),(22,'TB Encounter (Followup)','An encounter for a return visit to the TB clinic',2,'2025-10-23 15:05:07',0,NULL,NULL,NULL,'455bad1f-5e97-4ee9-9558-ff1df8808732',NULL,NULL,2,'2025-10-23 15:05:07'),(23,'Viral Load Non Suppressed Encounter','Viral Load Non Suppressed Follow up',2,'2025-10-23 15:05:07',0,NULL,NULL,NULL,'38cb2232-30fc-4b1f-8df1-47c795771ee9',NULL,NULL,2,'2025-10-23 15:05:07'),(24,'Appointment Follow-up','Followup actions for patients especially after missing a facility visit',2,'2025-10-23 15:05:07',0,NULL,NULL,NULL,'dc551efc-024d-4c40-aeb8-2147c4033778',NULL,NULL,2,'2025-10-23 15:05:07'),(25,'Triage','This is a form to capture information on triage. It include Vitals, global security indicators etc....',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'0f1ec66d-61db-4575-8248-94e10a88178f',NULL,NULL,2,'2025-10-23 15:05:08'),(26,'Medication Dispense','This encounter type is for dispensing of medication at facility',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'22902411-19c1-4a02-b19a-bf1a9c24fd51',NULL,NULL,2,'2025-10-23 15:05:08'),(27,'Missed Appointment Tracking','This encounter type is for tracking followup for missed appointments',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'791faefd-36b8-482f-ab78-20c297b03851',NULL,NULL,2,'2025-10-23 15:05:08'),(28,'Transfer In','Transfer in encounter',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'3e8354f7-31b3-4862-a52e-ff41a1ee60af',NULL,NULL,2,'2025-10-23 15:05:08'),(29,'Transfer Out','Transfer out encounter',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'e305d98a-d6a2-45ba-ba2a-682b497ce27c',NULL,NULL,2,'2025-10-23 15:05:08'),(30,'DR TB Summary (Enrollment)','An encounter for the initial visit to the Drug Resistance TB Program',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'0271ee3d-f274-49d1-b376-c842f075413f',NULL,NULL,2,'2025-10-23 15:05:08'),(31,'DR TB Encounter (Followup)','An encounter for a return visit to the Drug Resistance TB Program',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'41f8609d-e13b-4dff-8379-47ac5876512e',NULL,NULL,2,'2025-10-23 15:05:08'),(32,'ART Regimen Change','ART Regimen Switch or Change Encounter',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'c11774c1-3b4a-4bdb-a847-6060895e006d',NULL,NULL,2,'2025-10-23 15:05:08'),(33,'Covid19 Case Investigation','Covid19 Case Investigation Encounter',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'422ee220-9e83-451d-9b25-79a688a0413a',NULL,NULL,2,'2025-10-23 15:05:08'),(34,'Covid19 Clinical Management','Covid19 Clinical Management Encounter',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'cd9a2698-206f-44f3-a888-f824544413b4',NULL,NULL,2,'2025-10-23 15:05:08'),(35,'Covid19 Discharge','Covid19 Discharge Encounter',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'482e4b17-fb9c-4937-a1cf-9052d3e3be68',NULL,NULL,2,'2025-10-23 15:05:08'),(36,'Covid19 Postmortem','Covid19 Postmortem Encounter',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'7f7f59dc-defe-11eb-ba80-0242ac130004',NULL,NULL,2,'2025-10-23 15:05:08'),(37,'Covid19 Referral','Covid19 Referral Encounter',2,'2025-10-23 15:05:08',0,NULL,NULL,NULL,'afcdfcd8-defe-11eb-ba80-0242ac130004',NULL,NULL,2,'2025-10-23 15:05:08'),(38,'Emergency ART Service Encounter','Emergency ART Service Encounter',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'d18bd8f2-dfcd-11eb-ba80-0242ac130004',NULL,NULL,2,'2025-10-23 15:05:09'),(39,'CaCx Screening Eligibility Log Encounter','CaCx Screening Eligibility Log Encounter',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'91725548-2d90-4b28-be6d-0509ba37bb0f',NULL,NULL,2,'2025-10-23 15:05:09'),(40,'CaCx Treatment Encounter','CaCx Treatment Encounter',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'6d647bd4-33d6-4d04-a04a-595d2159b456',NULL,NULL,2,'2025-10-23 15:05:09'),(41,'Covid19 Vaccination Tracking Encounter','Covid19 vaccination tracking for clients active on ART',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'c392cf0e-5024-4f47-9ed9-e10e307e942f',NULL,NULL,2,'2025-10-23 15:05:09'),(42,'SMS Enrollment Encounter','SMS Enrollment Encounter',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'05fa932f-4203-43c9-8985-60f2bea8a773',NULL,NULL,2,'2025-10-23 15:05:09'),(43,'Family Planning Encounter Type','Family Planning Encounter Type',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'1668ef2e-1aca-4b5d-931d-696b272eea0f',NULL,NULL,2,'2025-10-23 15:05:09'),(44,'New Born In Patient Register','This is an encounter for new borns admitted.',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'121ce4fe-1279-4443-b391-0f3fd5b2168d',NULL,NULL,2,'2025-10-23 15:05:09'),(45,'In Patient Encounter','This is an encounter  for patients admitted into the hospital',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'5ef205bc-136a-469b-b074-5f39488db91a',NULL,NULL,2,'2025-10-23 15:05:09'),(46,'Child Health Encounter','This is an encounter assessing Children\'s Health',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'87a0d5b1-53dc-4269-bf39-ada0d5d16c0c',NULL,NULL,2,'2025-10-23 15:05:09'),(47,'SMC Follow up Encounter','SMS Follow up Encounter',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'d0f9e0b7-f336-43bd-bf50-0a7243857fa6',NULL,NULL,2,'2025-10-23 15:05:09'),(48,'Lab Request Encounter','Lab Request Encounter',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'cbf01392-ca29-11e9-a32f-2a2ae2dbcce4',NULL,NULL,2,'2025-10-23 15:05:09'),(49,'Facility Linkage Encounter','Facility Linkage Encounter',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'786c576b-b70f-4235-93ea-fbce1a3f38c4',NULL,NULL,2,'2025-10-23 15:05:09'),(50,'Mobility Screening  Encounter','Mobility Screening Encounter',2,'2025-10-23 15:05:09',0,NULL,NULL,NULL,'b57e1835-4ee2-47fa-9569-c700c39c169a',NULL,NULL,2,'2025-10-23 15:05:09'),(51,'ART Card - Family Tracking','This encounter supports tracking family members of a patient',2,'2025-10-23 15:05:10',0,NULL,NULL,NULL,'591cba6a-5c5f-11ee-8c99-0242ac120002',NULL,NULL,2,'2025-10-23 15:05:10'),(52,'DEATH AND CERTIFICATION','Notification of Death and Certification of Cause of Death',2,'2025-10-23 15:05:10',0,NULL,NULL,NULL,'e75c856a-9e91-4ffb-bf43-1b0450b4ff8c',NULL,NULL,2,'2025-10-23 15:05:10'),(53,'Registration','Encounter type for extra fields on registration',2,'2025-10-23 15:05:10',0,NULL,NULL,NULL,'1458b726-4a62-4444-be97-bb3e08c73745',NULL,NULL,2,'2025-10-23 15:05:10'),(54,'Medication Order','The encounter for ordering drugs for patient',2,'2025-10-23 15:05:10',0,NULL,NULL,NULL,'dbe038cd-cad5-439d-a761-a6d6d680219c',NULL,NULL,2,'2025-10-23 15:05:10'),(55,'TB Screening','Encounter type for screening TB patient',2,'2025-10-23 15:05:10',0,NULL,NULL,NULL,'08478ad9-ccc1-4cbe-9e55-473447984158',NULL,NULL,2,'2025-10-23 15:05:10'),(56,'Monkey Pox Screening','Encounter type for screening MPOX patient',2,'2025-10-23 15:05:10',0,NULL,NULL,NULL,'09478ad9-ccc1-4cbe-9e55-473447984158',NULL,NULL,2,'2025-10-23 15:05:10'),(57,'Procedure Results','Encounter type for processing procedure results',2,'2025-10-23 15:05:10',0,NULL,NULL,NULL,'a4870f6d-ea06-4bbe-b775-bcbfb0816dbf',NULL,NULL,2,'2025-10-23 15:05:10'),(58,'HIV Self Testing Encounter','Encounter type for to record clients accessing HIV Self-Test kits at Health facility and community distribution points',2,'2025-10-23 15:05:10',0,NULL,NULL,NULL,'b75fc5be-83a6-4771-afae-87d1b68af4f7',NULL,NULL,2,'2025-10-23 15:05:10'),(59,'HTS Contact Tracing Encounter','Encounter to record all clients eligible for index testing and SNS',2,'2025-10-23 15:05:10',0,NULL,NULL,NULL,'3849c43c-f14d-40ab-80d7-670cf251e525',NULL,NULL,2,'2025-10-23 15:05:11'),(60,'Early Infant Diagnosis (EID) Request',NULL,1,'2025-07-15 16:27:38',0,NULL,NULL,NULL,'d211f5a7-b0b7-4021-8224-72c58866600e',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `encounter_type` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-01 21:06:19
