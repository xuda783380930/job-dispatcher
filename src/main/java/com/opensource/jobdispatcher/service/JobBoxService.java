package com.opensource.jobdispatcher.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.opensource.jobdispatcher.dao.middleware.JobBoxDao;
import com.opensource.jobdispatcher.dao.middleware.TaskDao;
import com.opensource.jobdispatcher.dao.middleware.TaskStatusOptLogDao;
import com.opensource.jobdispatcher.model.middleware.JobBox;
import com.opensource.jobdispatcher.model.middleware.Task;
import com.opensource.jobdispatcher.model.middleware.TaskProcessStatus;
import com.opensource.jobdispatcher.model.middleware.TaskStatusOptLog;
import com.opensource.jobdispatcher.model.middleware.builder.JobBoxBuilder;
import com.opensource.jobdispatcher.model.middleware.builder.TaskBuilder;

@Service
public class JobBoxService{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JobBoxDao jobBoxDao;
	
	@Autowired 
	private TaskStatusOptLogDao taskStatusOptLogDao;
	
	@Autowired
	private TaskDao taskDao;
	
	private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    public JobBox createJobBox(JobBoxBuilder jobBoxBuilder){
    	JobBox jobBox = new JobBox();
    	//根据配置文件生成新的JobBox
    	LocalDateTime localDateTime = LocalDateTime.now();
    	String builderEventTime = DATE_FORMATTER.format(localDateTime);
    	List<Task> builderTasks = new ArrayList<Task>();
    	if(null!=jobBoxBuilder.getTaskBuilderList()) {
    		for(TaskBuilder taskBuilder:jobBoxBuilder.getTaskBuilderList()) {
    			Task task = new Task(taskBuilder,builderEventTime);
    			builderTasks.add(task);
    		}
    	}
    	jobBox.setJobCode(jobBoxBuilder.getJobCode());
    	jobBox.setEventTime(builderEventTime);
    	jobBox.setTasksList(builderTasks);
    	jobBoxDao.save(jobBox);
		return jobBox;
    }
    
	
	public boolean updateTaskStatus(Task t, TaskProcessStatus status,String optSeq) {
		LocalDateTime localDateTime = LocalDateTime.now();
		String logTime = DATE_FORMATTER.format(localDateTime);
		t.setTaskProcessStatus(status);
		taskDao.save(t);
		TaskStatusOptLog taskStatusOptLog = new TaskStatusOptLog();
		taskStatusOptLog.setJobCode(t.getJobCode());
		taskStatusOptLog.setTaskCode(t.getTaskCode());
		taskStatusOptLog.setEventTime(t.getEventTime());
		taskStatusOptLog.setBeforeStatus(t.getTaskProcessStatus());
		taskStatusOptLog.setAfterStatus(status);
		taskStatusOptLog.setOptTime(logTime);
		taskStatusOptLog.setOptSeq(optSeq);
		return null!=taskStatusOptLogDao.save(taskStatusOptLog);
	}
	
	public JobBox getJob(String jobCode,@NotNull String eventTime) {
		return jobBoxDao.findOneByJobCodeAndEventTime(jobCode,eventTime);
	}
	
	public List<JobBox> getJobList(String jobCode, String startDate, String endDate,Integer pageNum,Integer pageSize) {
		if(null == jobCode) {
			return jobBoxDao.findByEventTimeBetweenOrderByEventTime(startDate, endDate, PageRequest.of(pageNum,pageSize));
		}
		return jobBoxDao.findByJobCodeAndEventTimeBetweenOrderByEventTime(jobCode, startDate, endDate,PageRequest.of(pageNum,pageSize));
	}


	public List<JobBox> getAllUnfinishedJobOrderByEventTimeAsc() {
		return jobBoxDao.getAllUnfinishedJobOrderByEventTimeAsc();
	}
	
	

}
