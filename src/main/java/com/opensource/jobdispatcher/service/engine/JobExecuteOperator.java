package com.opensource.jobdispatcher.service.engine;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.opensource.jobdispatcher.model.middleware.JobBox;
import com.opensource.jobdispatcher.model.middleware.Task;
import com.opensource.jobdispatcher.model.middleware.TaskProcessStatus;
import com.opensource.jobdispatcher.model.middleware.builder.JobBoxBuilder;
import com.opensource.jobdispatcher.service.JobBoxBuilderRegisterCenter;
import com.opensource.jobdispatcher.service.JobBoxService;

@Service
public class JobExecuteOperator{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JobExecuteEngine jobExecuteEngine;
	
	@Autowired
	private JobBoxBuilderRegisterCenter jobBoxBuilderRegisterCenter;
	
	@Autowired
	private JobBoxService jobBoxService;
    
    @PostConstruct
	private void init(){
		List<JobBox> allJobBox = jobBoxService.getAllUnfinishedJobOrderByEventTimeAsc();
		if(null!=allJobBox) {
			log.info("####### init all jobBoxes size is " + allJobBox.size());
			for(JobBox jb:allJobBox) {
				JobBoxQueue jobBoxQueue = jobExecuteEngine.getJobQueueByCode(jb.getJobCode());
				pauseTriggeredTask(jb);
				jobBoxQueue.put(jb);
			}
		}
	}
    
    private void pauseTriggeredTask(JobBox jb) {
		List<Task> tl = jb.getAllTasks(TaskProcessStatus.TRIGGERED);
		for(Task task:tl ) {
			jobBoxService.updateTaskStatus(task, TaskProcessStatus.PAUSED, "sysRestart");
		}
	}

	public boolean triggerJob(String jobCode){
    	JobBoxBuilder jobBoxBuilder = jobBoxBuilderRegisterCenter.getJobBoxBuilderByJobCode(jobCode);
    	log.info("########" + jobBoxBuilder.getJobCode() + jobBoxBuilder.getTaskBuilderList().size());
    	JobBox jobBox = jobBoxService.createJobBox(jobBoxBuilder);
		if(null!=jobBox) {
			JobBoxQueue jobBoxQueue = jobExecuteEngine.getJobQueueByCode(jobBox.getJobCode());	
	    	jobBoxQueue.put(jobBox);
			log.info("Sucess put job:"+jobBox.getJobCode()+" to JobProcessQueue!");
			return true;
		}
		return false;
    }
    
	public Boolean updateTaskStatus(String jobCode,String taskCode,String eventTime,TaskProcessStatus status,String optSeq) {
		JobBoxQueue jobQueue = jobExecuteEngine.getJobQueueByCode(jobCode);
		if(null!=jobQueue) {
			JobBox jobBox = jobQueue.getJobBox(eventTime);
			return updateTaskStatus(jobBox,taskCode,status,optSeq);
		}
		return false;
	}

	public Boolean stopJob(String jobCode, String eventTime,String optSeq) {
		JobBoxQueue jobQueue = jobExecuteEngine.getJobQueueByCode(jobCode);
		if(null!=jobQueue) {
			JobBox jobBox = jobQueue.getJobBox(eventTime);
			return stopJobBox(jobBox,optSeq);
		}
		return false;
	}
	
	public Boolean startJob(String jobCode, String eventTime,String optSeq) {
		JobBoxQueue jobQueue = jobExecuteEngine.getJobQueueByCode(jobCode);
		if(null!=jobQueue) {
			JobBox jobBox = jobQueue.getJobBox(eventTime);
			return startJobBox(jobBox,optSeq);
		}
		return false;
	}
	
	public boolean stopQueue(String jobCode) {
		JobBoxQueue jobQueue = jobExecuteEngine.getJobQueueByCode(jobCode);
		if(null!=jobQueue) {
			jobQueue.blockQueue();
			return true;
		}
		return false;
	}

	public boolean startQueue(String jobCode) {
		JobBoxQueue jobQueue = jobExecuteEngine.getJobQueueByCode(jobCode);
		if(null!=jobQueue) {
			jobQueue.releaseQueue();
			return true;
		}
		return false;
	}
	
	private boolean updateTaskStatus(JobBox jobBox, String taskCode,TaskProcessStatus status, String optSeq) {
		if(!jobBox.upstreamTasksStatusAreALLFinished(taskCode)) {
			return false;
		}
		if(jobBox.downstreamTasksStatusAreStarted(taskCode)) {
			return false;
		}

		Task task = jobBox.getTask(taskCode);
		if(jobBoxService.updateTaskStatus(task,status,optSeq)){
			updateDownstreamTasksStatus(jobBox,taskCode,optSeq);
			return true;
		}
		return false;
	}
	
	private void updateDownstreamTasksStatus(JobBox jobBox,@NotNull String taskCode,String optSeq) {
		for(Task task:jobBox.getTasksList()) {
			List<String> upstreamTaskCodes = task.getUpstreamTaskCodes();
			for(String upstreamTaskCode:upstreamTaskCodes) {
				if(taskCode.equals(upstreamTaskCode)) {
					String downstreamTaskCode = task.getTaskCode();
					if(!TaskProcessStatus.UNSTART.equals(task.getTaskProcessStatus())) {//下游节点如果是UNSTART状态，无需修改状态
						Task downstreamTask = jobBox.getTask(downstreamTaskCode);
						if(jobBoxService.updateTaskStatus(downstreamTask,TaskProcessStatus.NEED_RESTART,optSeq)) {
							updateDownstreamTasksStatus(jobBox,downstreamTaskCode,optSeq);
						}
					}
				}
			}
		}
	}
	
	private Boolean stopJobBox(JobBox jobBox,String optSeq) {
		//可以执行但还未执行的任务先暂停
		List<Task> tasksCanExecute = jobBox.getTasksCanExecute();
		for(Task task:tasksCanExecute) {
			updateTaskStatus(jobBox, task.getTaskCode(),TaskProcessStatus.PAUSED,optSeq);
		}
			
		//正在执行的任务无法由引擎端停止，则将其下游任务暂停
		List<Task> tasksStarted = jobBox.getAllTasks(TaskProcessStatus.STARTED);
		for(Task task:tasksStarted) {
			List<String> downstreamTasks = jobBox.getJobStructVO().getDownstreamTasks(task.getTaskCode());
			for(String downstreamTaskCode:downstreamTasks) {
				updateTaskStatus(jobBox,downstreamTaskCode,TaskProcessStatus.PAUSED,optSeq);
			}
		}
			return true;
	}
	
	private Boolean startJobBox(JobBox jobBox,String optSeq) {
		List<Task> tasksPaused = jobBox.getAllTasks(TaskProcessStatus.PAUSED);
		for(Task task:tasksPaused) {
			updateTaskStatus(jobBox,task.getTaskCode(),TaskProcessStatus.NEED_RESTART,optSeq);
		}
		return true;
	}

}
