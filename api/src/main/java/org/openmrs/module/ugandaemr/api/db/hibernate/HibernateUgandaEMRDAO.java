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
package org.openmrs.module.ugandaemr.api.db.hibernate;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.APIException;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ugandaemr.api.db.UgandaEMRDAO;
import org.openmrs.module.ugandaemr.PublicHoliday;
import org.openmrs.module.ugandaemr.api.lab.OrderObs;
import org.openmrs.module.ugandaemr.api.model.NonPatientQueue;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * It is a default implementation of  {@link UgandaEMRDAO}.
 */
public class HibernateUgandaEMRDAO implements UgandaEMRDAO {
	protected final Log log = LogFactory.getLog(this.getClass());

	private SessionFactory sessionFactory;

	@Autowired
	DbSessionFactory dbSessionFactory;

	public DbSession getSession() {
		return dbSessionFactory.getCurrentSession();
	}

	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }

	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
	}

	public List<PublicHoliday> getAllPublicHolidays() {
		return (List<PublicHoliday>) getSessionFactory().getCurrentSession().createCriteria(PublicHoliday.class).list();
	}

	public PublicHoliday getPublicHolidayByDate(Date publicHolidayDate) throws APIException {
		return (PublicHoliday) getSessionFactory().getCurrentSession().createCriteria(PublicHoliday.class).add(Restrictions.eq("date", publicHolidayDate)).add(Restrictions.eq("voided", false)).uniqueResult();
	}

	public PublicHoliday savePublicHoliday(PublicHoliday publicHoliday) {
        getSessionFactory().getCurrentSession().saveOrUpdate(publicHoliday);
		return publicHoliday;
	}

	@Override
	public PublicHoliday getPublicHolidaybyUuid(String uuid) {
		return (PublicHoliday) getSessionFactory().getCurrentSession().createCriteria(PublicHoliday.class).add(Restrictions.eq("uuid", uuid))
		.uniqueResult();
	}

	@Override
	public List<PublicHoliday> getPublicHolidaysByDate(Date publicHolidayDate) {
		return (List<PublicHoliday>) getSessionFactory().getCurrentSession().createCriteria(PublicHoliday.class).add(Restrictions.eq("date", publicHolidayDate)).list();
	}

	/**
	 * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#saveOrderObs(org.openmrs.module.ugandaemr.api.lab.OrderObs)
	 */
	@Override
	public OrderObs saveOrderObs(OrderObs orderObs) {
		sessionFactory.getCurrentSession().saveOrUpdate(orderObs);
		return orderObs;
	}

	/**
	 * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObs(org.openmrs.Encounter, java.util.Date, java.util.Date, java.util.List, java.util.List,boolean)
	 */
	public List<OrderObs> getOrderObs(Encounter encounter, Date onOrBefore, Date onOrAfter, List<Order> orders, List<Obs> obs,boolean includeVoided) {

		Criteria crit = sessionFactory.getCurrentSession().createCriteria(OrderObs.class);
		if (encounter != null) {
			crit.add(Restrictions.eq("encounter", encounter));
		}

		if (orders != null) {
			crit.add(Restrictions.in("order", orders));
		}

		if (obs != null) {
			crit.add(Restrictions.in("obs", obs));
		}

		if (onOrAfter != null) {
			crit.add(Restrictions.ge("dateCreated", OpenmrsUtil.firstSecondOfDay(onOrAfter)));
		}

		if (onOrBefore != null) {
			crit.add(Restrictions.le("dateCreated", OpenmrsUtil.getLastMomentOfDay(onOrBefore)));
		}

		if (!includeVoided) {
			crit.add(Restrictions.eq("voided", false));
		}

		crit.addOrder(org.hibernate.criterion.Order.desc("dateCreated"));

		return crit.list();
	}

	/**
	 * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObsByObs(org.openmrs.Obs)
	 */
	@Override
	public OrderObs getOrderObsByObs(Obs obs) {
		return (OrderObs) sessionFactory.getCurrentSession().createCriteria(OrderObs.class).add(Restrictions.eq("obs", obs)).uniqueResult();
	}

	/**
	 * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObsByObs(org.openmrs.Obs)
	 */
	@Override
	public OrderObs getOrderObsByUuid(String uuid) {
		return (OrderObs) sessionFactory.getCurrentSession().createCriteria(OrderObs.class).add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	/**
	 * @see org.openmrs.module.ugandaemr.api.UgandaEMRService#getOrderObsById(java.lang.Integer)
	 */
	@Override
	public OrderObs getOrderObsById(Integer orderObsId) {
		return (OrderObs) sessionFactory.getCurrentSession().createCriteria(OrderObs.class).add(Restrictions.eq("orderObsId", orderObsId)).uniqueResult();
	}

	public OrderObs getOrderObsByOrder(Order order){
		return (OrderObs) sessionFactory.getCurrentSession().createCriteria(OrderObs.class).add(Restrictions.eq("order", order)).uniqueResult();
	}


	@Override
	public NonPatientQueue saveNonPatientQueue(NonPatientQueue queue) {
		sessionFactory.getCurrentSession().saveOrUpdate(queue);
		return queue;
	}

	@Override
	public NonPatientQueue getNonPatientQueueById(Integer id) {
		return (NonPatientQueue) sessionFactory.getCurrentSession().get(NonPatientQueue.class, id);
	}

	@Override
	public NonPatientQueue getNonPatientQueueByUuid(String uuid) {
		return (NonPatientQueue) sessionFactory.getCurrentSession()
				.createQuery("from NonPatientQueue q where q.uuid = :uuid")
				.setParameter("uuid", uuid)
				.uniqueResult();
	}

	@Override
	public List<NonPatientQueue> getNonPatientQueueByTicketNumber(String ticketNumber, Date fromDate, Date toDate) {

		Criteria criteria = getSession().createCriteria(NonPatientQueue.class);

		criteria.add(Restrictions.between("dateCreated", fromDate, toDate));
		criteria.add(Restrictions.in("ticketNumber", ticketNumber));
		criteria.add(Restrictions.in("voided", false));

		return criteria.list();
	}

	@Override
	public List<NonPatientQueue> getNonPatientQueuesByQueueRoom(List<Location> queueRoom, Date fromDate, Date toDate) {

		Criteria criteria = getSession().createCriteria(NonPatientQueue.class);

		if (queueRoom != null)
			criteria.add(Restrictions.in("queueRoom", queueRoom));
		criteria.add(Restrictions.between("dateCreated", fromDate, toDate));

		criteria.addOrder(org.hibernate.criterion.Order.asc("dateCreated"));
		return criteria.list();
	}

	@Override
	public List<NonPatientQueue> getNonPatientQueuesByQueueRoomAndStatus(List<Location> queueRoom, NonPatientQueue.NonPatientQueueStatus status) {

		Criteria criteria = getSession().createCriteria(NonPatientQueue.class);
		if (status != null) {
			criteria.add(Restrictions.eq("status", status));
		}

		if (queueRoom != null) {
			criteria.add(Restrictions.eq("queueRoom", queueRoom));
		}

		criteria.add(Restrictions.in("voided", false));

		criteria.addOrder(org.hibernate.criterion.Order.desc("dateCreated"));


		return criteria.list();
	}

	@Override
	public List<NonPatientQueue> getAllActiveNonPatientQueues() {
		Criteria criteria = getSession().createCriteria(NonPatientQueue.class);
		criteria.add(Restrictions.in("status", NonPatientQueue.NonPatientQueueStatus.WAITING,NonPatientQueue.NonPatientQueueStatus.CALLED,NonPatientQueue.NonPatientQueueStatus.ARRIVED,NonPatientQueue.NonPatientQueueStatus.SERVING));

		criteria.add(Restrictions.in("voided", false));

		criteria.addOrder(org.hibernate.criterion.Order.desc("dateCreated"));

		return criteria.list();
	}

	@Override
	public void deleteNonPatientQueue(NonPatientQueue queue) {
		sessionFactory.getCurrentSession().delete(queue);
	}


}