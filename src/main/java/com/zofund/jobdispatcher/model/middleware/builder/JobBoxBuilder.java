package com.zofund.jobdispatcher.model.middleware.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
    
    public JobBoxBuilder() {
    	
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
	
	public List<TaskBuilder> getDownstreamTasks(TaskBuilder nowTaskBuilder) {
		List<TaskBuilder> downstreamTasks = new ArrayList<TaskBuilder>();
		if(null!=this&&null!=this.getTaskBuilderList()) {
			for(TaskBuilder task:this.getTaskBuilderList()) {
				String upstreamTaskCodeStr = task.getUpstreamTaskCodes();
				if(null == upstreamTaskCodeStr){
					if(null == nowTaskBuilder) {
						downstreamTasks.add(task);
					}
				}else{
					String[] upstreamTaskCodes = upstreamTaskCodeStr.split(",");
					for(String upstreamTaskCode:upstreamTaskCodes) {
						if(null != nowTaskBuilder&&upstreamTaskCode.equals(nowTaskBuilder.getTaskCode())) {
							downstreamTasks.add(task);
						}
					}
				}
			}
		}
		return downstreamTasks;
	}
	
    public HashSet<String> getDuplicatedLinkNodes(TaskBuilder nowTask){
		List<TaskBuilder> downStreamTasks = this.getDownstreamTasks(nowTask);
		if(downStreamTasks!=null) {
			for(int i=0;i<downStreamTasks.size();i++) {
				if(null!=getDuplicatedLinkNodes(downStreamTasks.get(i))) {
					return getDuplicatedLinkNodes(downStreamTasks.get(i));
				}
				for(int j=i+1;j<downStreamTasks.size();j++) {
					if(isIn(downStreamTasks.get(i),downStreamTasks.get(j))){
						HashSet<String> hs = new HashSet<String>();
						hs.add(nowTask.getTaskCode());
						hs.add(downStreamTasks.get(i).getTaskCode());
						hs.add(downStreamTasks.get(j).getTaskCode());
						return hs ;
					}
				}
			}
		}
		return null;
	}
    
	private boolean isIn(TaskBuilder taskBuilder, TaskBuilder taskGroup) {
		if(taskBuilder==taskGroup) {
			return true;
		}
		List<TaskBuilder> downStreamTasks = this.getDownstreamTasks(taskGroup);
		if(downStreamTasks!=null) {
			for(TaskBuilder tb:downStreamTasks) {
				if(isIn(taskBuilder,tb)) {
					return true;
				}
			}
		}
		return false;
	}

	public TaskBuilder getTaskBuilderByTaskCode(String taskCode) {
		for(TaskBuilder taskBuilder:taskBuilderList) {
			if(taskCode.equals(taskBuilder.getTaskCode())) {
				return taskBuilder;
			}
		}
		return null;
	}

}
