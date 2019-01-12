package com.zofund.jobdispatcher.model.middleware;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zofund.jobdispatcher.model.middleware.builder.TaskBuilder;

@Entity
@Table(name = "T_TASK")
public class Task implements Serializable{
	
	private static final long serialVersionUID = 7639002185742196449L;

	@Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "C_JOBCODE", nullable = false, length = 2000)
	private String jobCode;

	@Column(name = "C_TASKCODE", nullable = false, length = 2000)
	private String taskCode;
	
	@Column(name = "C_EVENTTIME", nullable = false, length = 2000) 
	private String eventTime;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "C_TASKPROCESSSTATUS", nullable = false, length = 2000)
	private TaskProcessStatus taskProcessStatus;

	@Column(name = "C_ADAPTERSPRINGID",  nullable = false, length = 2000) 
	private String adapterSpringId;

	@Column(name = "C_ADAPTERMETHODNAME",  nullable = false, length = 2000) 
	private String adapterMethodName;
	
	@Column(name = "C_ADAPTERPARA",  nullable = false, length = 4000) 
	private String adapterPara;

	@Column(name = "C_UPSTREAMTASKCODES", nullable = true, length = 4000) 
	private String upstreamTaskCodes;
	
	@Column(name = "C_OVERTIMEMINUTE", nullable = true, length = 2000) 
	private String overTimeMinute;
	
	public Task(){
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}

	public void setAdapterSpringId(String adapterSpringId) {
		this.adapterSpringId = adapterSpringId;
	}

	public void setAdapterMethodName(String adapterMethodName) {
		this.adapterMethodName = adapterMethodName;
	}

	public void setAdapterPara(String adapterPara) {
		this.adapterPara = adapterPara;
	}

	public void setUpstreamTaskCodes(String upstreamTaskCodes) {
		this.upstreamTaskCodes = upstreamTaskCodes;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
	
	public Task(TaskBuilder taskBuilder,String eventTime) {
		this.jobCode = taskBuilder.getJobCode();
		this.taskCode = taskBuilder.getTaskCode();
		this.upstreamTaskCodes = taskBuilder.getUpstreamTaskCodes();
		this.adapterSpringId = taskBuilder.getAdapterSpringId();
		this.adapterMethodName = taskBuilder.getAdapterMethodName();
		this.adapterPara = taskBuilder.getAdapterPara();
		this.eventTime = eventTime;
		this.taskProcessStatus = TaskProcessStatus.UNSTART;
	}
	
    public JsonObject toJsonObject(){
		JsonObject taskJson = new JsonObject();
		taskJson.addProperty("taskCode", this.getTaskCode());
		taskJson.addProperty("adapterSpringId", this.getAdapterSpringId());
		taskJson.addProperty("adapterMethodName", this.getAdapterMethodName());
		taskJson.addProperty("adapterPara", this.getAdapterPara());
		taskJson.addProperty("eventTime", this.getEventTime().toString());
		taskJson.addProperty("taskProcessStatus", this.getTaskProcessStatus().toString());
		
		String upstreamTaskCodeStr = this.getUpstreamTaskCodes();
		String[] upstreamTaskC = upstreamTaskCodeStr.split(",");
		if( null!= upstreamTaskCodes ) {
			JsonArray upstreamTaskCodesJsonArr = new JsonArray();
			for(String upstreamTaskCode:upstreamTaskC) {
				upstreamTaskCodesJsonArr.add(upstreamTaskCode);
			}
			taskJson.add("upstreamTaskCodes", upstreamTaskCodesJsonArr);
		}
	
    	return taskJson;
    }

	public String getAdapterSpringId() {
		return adapterSpringId;
	}

	public String getTaskCode() {
		return taskCode;
	}

	public String getAdapterMethodName() {
		return adapterMethodName;
	}
	
	public String getAdapterPara() {
		return adapterPara;
	}
	
	public String getUpstreamTaskCodes() {
		return upstreamTaskCodes;
	}

	public String getJobCode() {
		return jobCode;
	}

	public String getEventTime() {
		return eventTime;
	}

	public TaskProcessStatus getTaskProcessStatus() {
		return taskProcessStatus;
	}

	public void setTaskProcessStatus(TaskProcessStatus taskProcessStatus) {
		this.taskProcessStatus = taskProcessStatus;
	}
	
	public String getOverTimeMinute() {
		return overTimeMinute;
	}

	public void setOverTimeMinute(String overTimeMinute) {
		this.overTimeMinute = overTimeMinute;
	}

}
