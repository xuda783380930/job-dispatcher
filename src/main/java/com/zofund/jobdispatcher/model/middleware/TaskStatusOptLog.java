package com.zofund.jobdispatcher.model.middleware;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_TASK_OPT_LOG")
public class TaskStatusOptLog implements Serializable{

	private static final long serialVersionUID = 8790686410504761107L;

	@Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "C_JOBCODE", length = 2000)
	private String jobCode;
	
	@Column(name = "C_TASKCODE", length = 2000)
	private String taskCode;
	
	@Column(name = "C_EVENTTIME", length = 2000)
	private  String eventTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_BEFORESTATUS", length = 2000)
	private TaskProcessStatus beforeStatus;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "C_AFTERSTATUS", length = 2000)
	private TaskProcessStatus afterStatus;	
	
	@Column(name = "C_OPTTIME",length = 2000)
	private  String optTime;
	
	@Column(name = "C_OPTSEQ",length = 2000)
	private  String optSeq;
	
	public TaskStatusOptLog(){
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

	public String getTaskCode() {
		return taskCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public String getEventTime() {
		return eventTime;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public TaskProcessStatus getBeforeStatus() {
		return beforeStatus;
	}

	public void setBeforeStatus(TaskProcessStatus beforeStatus) {
		this.beforeStatus = beforeStatus;
	}

	public TaskProcessStatus getAfterStatus() {
		return afterStatus;
	}

	public void setAfterStatus(TaskProcessStatus afterStatus) {
		this.afterStatus = afterStatus;
	}

	public String getOptTime() {
		return optTime;
	}

	public void setOptTime(String optTime) {
		this.optTime = optTime;
	}

	public String getOptSeq() {
		return optSeq;
	}

	public void setOptSeq(String optSeq) {
		this.optSeq = optSeq;
	}
}
