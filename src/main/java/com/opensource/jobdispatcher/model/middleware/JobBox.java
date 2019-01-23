package com.opensource.jobdispatcher.model.middleware;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import com.opensource.jobdispatcher.model.middleware.vo.JobStructVO;

@Entity
@Table(name = "T_JOBBOX")
public class JobBox implements Serializable{
	
	private static final long serialVersionUID = 1081598993967570859L;

	@Id
    @GeneratedValue
    @Column(name = "C_JOBBOX_ID", nullable = false, length = 2000)
    private Long id;
	
	@Column(name = "C_JOBCODE", nullable = false, length = 2000)
	private String jobCode;
	
	@Column(name = "C_EVENTTIME", nullable = false, length = 2000)
	private String eventTime;

    @OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
    @JoinColumn(name = "C_JOBBOX_ID")
	private List<Task> tasksList;
    
	public JobBox(){
		
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

	public String getEventTime() {
		return eventTime;
	}

    //辅助结构
    @Transient
    private JobStructVO  jobStructVO = null;
    
    @Transient
    private Map<String,Task> taskMap = null;

	public Task getTask(String taskCode) {
    	if(null == taskMap) {
    		taskMap = new HashMap<String,Task>();
    		if(null!=tasksList) {
    			for(Task t:tasksList) {
    				taskMap.put(t.getTaskCode(), t);
    			}
    		}
    	}
		return taskMap.get(taskCode);
	}
	
    public JobStructVO getJobStructVO(){
		if(null==jobStructVO) {
    		jobStructVO = new JobStructVO(tasksList);
    	}
    	return jobStructVO;
    }
    
	public List<Task> getTasksList() {
		return tasksList;
	}

	public void setTasksList(List<Task> tasksList) {
		this.tasksList = tasksList;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public List<Task> getTasksCanExecute() {
		List<Task> tasksCanExecute = new ArrayList<Task>();
		for(Task t:this.getTasksList()) {
			if(canExecute(t)) {
				tasksCanExecute.add(t);
			}
		}
		return tasksCanExecute;
	}
	
	public Boolean allTaskIsFinished() {
		for(Task task:this.getTasksList()) {
				if(!TaskProcessStatus.FINISHED.equals(task.getTaskProcessStatus())) {
					return false;
				}
		}
		return true;
	}
	
	public List<Task> getAllTasks(@NotNull TaskProcessStatus status) {
		List<Task> tasks = new ArrayList<Task>();
		for(Task task:this.tasksList) {
			if(status.equals(task.getTaskProcessStatus())) {
				tasks.add(task);
			}
		}
		return tasks;
	}
	
	private boolean canExecute(Task t) {
		TaskProcessStatus taskStatus = t.getTaskProcessStatus();
		if(TaskProcessStatus.UNSTART == taskStatus //当前状态未开始或者需要重新开始的任务
				||TaskProcessStatus.NEED_RESTART == taskStatus) {
			List<String> upstreamTaskCodes = t.getUpstreamTaskCodes();
			for(String upstreamTaskCode:upstreamTaskCodes){
				if(null == getTask(upstreamTaskCode)) {
					return false;
				}
				if(!TaskProcessStatus.FINISHED.equals(getTask(upstreamTaskCode).getTaskProcessStatus())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public  JobProcessStatus getJobStatus(){
			JobProcessStatus jobProcessStatus = JobProcessStatus.UNSTART;
		 	boolean started = false;
		 	boolean finished = true;
	 		for(Task t:this.tasksList) {
	 			if(TaskProcessStatus.UNSTART != t.getTaskProcessStatus()) {
	 				started = true;
	 			}
		 		if(TaskProcessStatus.FINISHED != t.getTaskProcessStatus()) {
		 			finished = false;
		 		}
	 		}

			if(started) {
				if(finished) {
					jobProcessStatus = JobProcessStatus.FINISHED;
				}else {
					jobProcessStatus = JobProcessStatus.STARTED;
				}
			}
	 	
	     	return jobProcessStatus;
	     }
	
	public Boolean downstreamTasksStatusAreStarted(@NotNull String taskCode) {
		jobStructVO = getJobStructVO();
		List<String> downstreamTasks = jobStructVO.getDownstreamTasks(taskCode);
		for(String downstreamTaskCode : downstreamTasks) {
			Task task = this.getTask(downstreamTaskCode);
			if(task!=null) {
				if(TaskProcessStatus.STARTED.equals(task.getTaskProcessStatus())||
							downstreamTasksStatusAreStarted(downstreamTaskCode)) {
				return true;
				}
			}
		}
		return false;
	}
	
	public Boolean upstreamTasksStatusAreALLFinished(String taskCode) {
		Task task = this.getTask(taskCode);
		if(null==task) {
			return false;
		}
		List<String> upstreamTaskCodes = task.getUpstreamTaskCodes();
		for(String upstreamTaskCode:upstreamTaskCodes) {
			if(!upstreamTasksStatusAreALLFinished(upstreamTaskCode)||
					!TaskProcessStatus.FINISHED.equals(this.getTask(upstreamTaskCode).getTaskProcessStatus())) {
				return false;
			}
		}
		return true;	
	}

}
