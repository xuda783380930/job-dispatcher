package com.zofund.jobdispatcher.model.middleware.builder;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_TASK_BUILDER")
public class TaskBuilder implements Serializable{

	private static final long serialVersionUID = 3534715120209752314L;

	@Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "C_JOBCODE",nullable=false, length = 2000)
	private String jobCode;
	
	@Column(name = "C_TASKCODE",nullable=false, length = 2000)
	private String taskCode;
	
	@Column(name = "C_ADAPTERSPRINGID",nullable=false, length = 2000) 
	private String adapterSpringId;
	
	@Column(name = "C_ADAPTERMETHODNAME",nullable=false, length = 2000) 
	private String adapterMethodName;
	
	@Column(name = "C_ADAPTERPARA",nullable=false, length = 4000) 
	private String adapterPara;
	
	@Column(name = "C_UPSTREAMTASKCODES", nullable = true, length = 4000) 
	private String upstreamTaskCodes;
	
	@Column(name = "C_OVERTIMEMINUTE", nullable = true, length = 2000) 
	private Long overTimeMinute = 24*60L; //默认24*60分钟算超时
	
	public TaskBuilder(){
		
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public String getAdapterSpringId() {
		return adapterSpringId;
	}

	public void setAdapterSpringId(String adapterSpringId) {
		this.adapterSpringId = adapterSpringId;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public String getAdapterMethodName() {
		return adapterMethodName;
	}

	public void setAdapterMethodName(String adapterMethodName) {
		this.adapterMethodName = adapterMethodName;
	}

	public String getAdapterPara() {
		return adapterPara;
	}

	public void setAdapterPara(String adapterPara) {
		this.adapterPara = adapterPara;
	}

	public Long getOverTimeMinute() {
		return overTimeMinute;
	}

	public void setOverTimeMinute(Long overTimeMinute) {
		this.overTimeMinute = overTimeMinute;
	}

	public String getUpstreamTaskCodes() {
		return upstreamTaskCodes;
	}

	public void setUpstreamTaskCodes(String upstreamTaskCodes) {
		this.upstreamTaskCodes = upstreamTaskCodes;
	}

}
