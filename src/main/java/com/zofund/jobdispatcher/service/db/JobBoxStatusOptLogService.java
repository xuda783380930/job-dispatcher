package com.zofund.jobdispatcher.service.db;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zofund.jobdispatcher.dao.middleware.TaskStatusOptLogDao;
import com.zofund.jobdispatcher.model.middleware.TaskProcessStatus;
import com.zofund.jobdispatcher.model.middleware.TaskStatusOptLog;

@Service
public class JobBoxStatusOptLogService{
	
	@Autowired
	private TaskStatusOptLogDao taskStatusOptLogDao;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public TaskStatusOptLog getStatusOptTime(String jobCode, String eventTime,String taskCode,TaskProcessStatus status) {
		List<TaskStatusOptLog> results =  taskStatusOptLogDao.
				findByJobCodeAndEventTimeAndTaskCodeAndAfterStatusOrderById(jobCode, eventTime, taskCode, status);
		if(null!=results&&results.size()>0) {
			return results.get(0);
		}
		return null;
				
	}
	
	public List<TaskStatusOptLog> getStatusOptLog(String jobCode, String eventTime, String taskCode) {
		return taskStatusOptLogDao.findAllByJobCodeAndEventTimeAndTaskCodeOrderById(jobCode, eventTime, taskCode);
	}
	
	public String getStartedTime(String jobCode, String eventTime,String taskCode) {
		return taskStatusOptLogDao.getStartedTime(jobCode ,taskCode ,eventTime);
	}

}
