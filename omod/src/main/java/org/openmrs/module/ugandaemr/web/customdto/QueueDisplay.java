package org.openmrs.module.ugandaemr.web.customdto;

import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.Date;
import java.util.List;

public class QueueDisplay {
	
	private SimpleObject context;
	
	private List<SimpleObject> nowServing;
	
	private List<SimpleObject> upNext;
	
	private SimpleObject stats;
	
	private Date lastUpdated;
	
	public SimpleObject getContext() {
		return context;
	}
	
	public void setContext(SimpleObject context) {
		this.context = context;
	}
	
	public List<SimpleObject> getNowServing() {
		return nowServing;
	}
	
	public void setNowServing(List<SimpleObject> nowServing) {
		this.nowServing = nowServing;
	}
	
	public List<SimpleObject> getUpNext() {
		return upNext;
	}
	
	public void setUpNext(List<SimpleObject> upNext) {
		this.upNext = upNext;
	}
	
	public SimpleObject getStats() {
		return stats;
	}
	
	public void setStats(SimpleObject stats) {
		this.stats = stats;
	}
	
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
