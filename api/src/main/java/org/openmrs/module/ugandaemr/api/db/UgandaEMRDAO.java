/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.ugandaemr.api.db;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.module.ugandaemr.api.UgandaEMRService;
import org.openmrs.module.ugandaemr.PublicHoliday;
import org.openmrs.module.ugandaemr.api.lab.OrderObs;

/**
 *  Database methods for {@link UgandaEMRService}.
 */
public interface UgandaEMRDAO {
	
	/*
	 * Add DAO methods here
	 */

	public List<PublicHoliday> getAllPublicHolidays();

	public PublicHoliday getPublicHolidayByDate(Date publicHolidayDate);

	public PublicHoliday savePublicHoliday(PublicHoliday publicHolidays);

	public PublicHoliday getPublicHolidaybyUuid(String uuid);

	public List<PublicHoliday> getPublicHolidaysByDate(Date publicHolidayDate);

	public List<Map<String, Object>> getCrddpPharmacies(String cohortTypeUuid);

	/**
	 * Finds encounters whose laboratory orders received results and were marked
	 * completed within the supplied period.
	 *
	 * @param from lower bound for the result observation timestamp
	 * @param to upper bound for the result observation timestamp
	 * @return distinct encounter ids that have completed laboratory results
	 */
	public List<Integer> getCompletedLabResultEncounterIds(Date from, Date to);

	/**
	 * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#saveOrderObs(org.openmrs.module.ugandaemr.api.lab.OrderObs)
	 */
    public OrderObs saveOrderObs(OrderObs orderObs);

	/**
	 * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObs(org.openmrs.Encounter, java.util.Date, java.util.Date, java.util.List, java.util.List,boolean)
	 */
    public List<OrderObs> getOrderObs(Encounter encounter, Date onOrBefore, Date onOrAfter, List<Order> orders, List<Obs> obs,boolean includeVoided);

	/**
	 * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObsByObs(org.openmrs.Obs)
	 */
	public OrderObs getOrderObsByObs(Obs obs);

	/**
	 * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObsByUuid(String)
	 */
	public OrderObs getOrderObsByUuid(String uuid);

	/**
	 * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObsById(java.lang.Integer)
	 */
	public OrderObs getOrderObsById(Integer orderObsId);

	public OrderObs getOrderObsByOrder(Order order);
}
