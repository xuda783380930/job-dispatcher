package com.zofund.jobdispatcher.model.middleware.builder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.zofund.jobdispatcher.model.middleware.vo.JobStructVO;

@Entity
@Table(name = "T_JOB_BUILDER")
public class JobBoxBuilder implements Serializable {
	
	private static final long serialVersionUID = -1649394169158442970L;

	@Id
	@Column(name = "C_JOBCODE",nullable=false, length = 2000)
	private String jobCode;

	@Column(name = "C_MONITOR_PARAS", nullable = true, length = 2000)
	private String monitorParas;//短信收信人等信息
	
    @OneToMany(cascade={CascadeType.ALL},fetch=FetchType.EAGER)
    @JoinColumn(name = "C_JOBCODE")
	private List<TaskBuilder> taskBuilderList;
    
    //辅助结构
    @Transient
    private JobStructVO  jobStructVO = null;
    
    @Transient
    private Map<String,TaskBuilder> taskBuilderMap = null;
    
    public JobBoxBuilder() {
    	
    }
    
    public JobStructVO getJobStructVO(){
		if(null==jobStructVO) {
    		jobStructVO = new JobStructVO(taskBuilderList);
    	}
    	return jobStructVO;
    }

	public List<TaskBuilder> getTaskBuilderList() {
		return taskBuilderList;
	}

	public void setTaskBuilderList(List<TaskBuilder> taskBuilderList) {
		this.taskBuilderList = taskBuilderList;
	}

	public String getJobCode() {
		return jobCode;
	}

	public String getMonitorParas() {
		return monitorParas;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public void setMonitorParas(String monitorParas) {
		this.monitorParas = monitorParas;
	}

	public TaskBuilder getTaskBuilderByTaskCode(String taskCode) {
    	if(null==taskBuilderMap) {
    		taskBuilderMap = new HashMap<String,TaskBuilder>();
    		if(null!=taskBuilderList) {
    			for(TaskBuilder tb:taskBuilderList) {
    				taskBuilderMap.put(tb.getTaskCode(), tb);
    			}
    		}
    	}
		return taskBuilderMap.get(taskCode);
	}

}
