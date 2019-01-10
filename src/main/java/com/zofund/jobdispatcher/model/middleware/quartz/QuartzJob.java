package com.zofund.jobdispatcher.model.middleware.quartz;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_QUARTZJOB_BUILDER")
public class QuartzJob implements Serializable{

	private static final long serialVersionUID = 5444729138327365975L;
	
	@Id
	@Column(name = "C_JOBCODE",nullable=false, length = 2000)
	private String jobCode;
	
	@Column(name = "C_JOBCRONEXPRESSION",nullable=false, length = 2000)
	private String jobCronExpression;
	
	@Column(name = "C_FILTERCALENDARNAME", nullable = true,length = 2000)
	private String filterCalendarName;

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}
	
	public void setJobCronExpression(String jobCronExpression) {
		this.jobCronExpression = jobCronExpression;
	}

	public void setFilterCalendarName(String filterCalendarName) {
		this.filterCalendarName = filterCalendarName;
	}
	
	public String getJobCronExpression() {
		return jobCronExpression;
	}
	public String getFilterCalendarName() {
		return filterCalendarName;
	}

}
