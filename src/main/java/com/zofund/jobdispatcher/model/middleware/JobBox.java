package com.zofund.jobdispatcher.model.middleware;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public JobBox(){
		
	}
	
	public String getJobCode() {
		return jobCode;
	}

	public String getEventTime() {
		return eventTime;
	}
	

	public List<Task> getDownstreamTasks(String taskCode) {
		ArrayList<Task> downstreamTasks = new ArrayList<Task>();
		for(Task task:tasksList){
			if( null == task.getUpstreamTaskCodes()){
				if(null == taskCode) {
					downstreamTasks.add(task);
				}
			}else{
				String[] upstreamTaskCodes = task.getUpstreamTaskCodes().split(",");
				for(String upstreamTaskCode:upstreamTaskCodes) {
					if(upstreamTaskCode.equals(taskCode)) {
						if(null != taskCode) {
							downstreamTasks.add(task);
						}
					}
				}
			}
		}
		return downstreamTasks;
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
	
	public synchronized Boolean allTaskIsFinished() {
		for(Task task:this.getTasksList()) {
				if(!TaskProcessStatus.FINISHED.equals(task.getTaskProcessStatus())) {
					return false;
				}
		}
		return true;
	}

	public Task getTask(String taskCode) {
		for(Task task:this.tasksList) {
			if(taskCode.equals(task.getTaskCode())) {
				return task;
			}
		}
		return null;
	}
	
	public List<Task> getTasksStarted() {
		ArrayList<Task> tasksStarted = new ArrayList<Task>();
		for(Task task:this.tasksList) {
			if(TaskProcessStatus.STARTED.equals(task.getTaskProcessStatus())) {
				tasksStarted.add(task);
			}
		}
		return tasksStarted;
	}
	
	public List<Task> getTasksPaused() {
		ArrayList<Task> tasksStarted = new ArrayList<Task>();
		for(Task task:this.tasksList) {
			if(TaskProcessStatus.PAUSED.equals(task.getTaskProcessStatus())) {
				tasksStarted.add(task);
			}
		}
		return tasksStarted;
	}
	
	private boolean canExecute(Task t) {
		TaskProcessStatus taskStatus = t.getTaskProcessStatus();
		if(TaskProcessStatus.UNSTART == taskStatus //当前状态未开始或者需要重新开始的任务
				||TaskProcessStatus.NEED_RESTART == taskStatus) {
			if(null!=t.getUpstreamTaskCodes()) {
				String[] upstreamTaskCodes = t.getUpstreamTaskCodes().split(",");
				for(String upstreamTaskCode:upstreamTaskCodes){
					if(null == getTask(upstreamTaskCode)) {
						return false;
					}
					if(!TaskProcessStatus.FINISHED.equals(getTask(upstreamTaskCode).getTaskProcessStatus())) {
						return false;
					}
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
		for(Task task:this.getTasksList()) {
			if(null!=task.getUpstreamTaskCodes()) {
				String[] upstreamTaskCodes = task.getUpstreamTaskCodes().split(",");
				for(String upstreamTaskCode:upstreamTaskCodes) {
					if(taskCode.equals(upstreamTaskCode)) {
						String downstreamTaskCode = task.getTaskCode();
						if(TaskProcessStatus.STARTED.equals(task.getTaskProcessStatus())||
								downstreamTasksStatusAreStarted(downstreamTaskCode)) {
							return true;
						}
					}
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
		if(null!=task.getUpstreamTaskCodes()) {
			String[] upstreamTaskCodes = task.getUpstreamTaskCodes().split(",");
			for(String upstreamTaskCode:upstreamTaskCodes) {
				if(!upstreamTasksStatusAreALLFinished(upstreamTaskCode)||
						!TaskProcessStatus.FINISHED.equals(this.getTask(upstreamTaskCode).getTaskProcessStatus())) {
					return false;
				}
			}
		}
		return true;	
	}

}
