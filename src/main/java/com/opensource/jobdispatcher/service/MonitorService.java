package com.opensource.jobdispatcher.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.opensource.jobdispatcher.model.middleware.JobBox;
import com.opensource.jobdispatcher.model.middleware.Task;
import com.opensource.jobdispatcher.model.middleware.TaskProcessStatus;
import com.opensource.jobdispatcher.service.db.JobBoxStatusOptLogService;
import com.opensource.jobdispatcher.service.engine.JobBoxQueue;
import com.opensource.jobdispatcher.service.engine.JobExecuteEngine;

@Service
@EnableScheduling
public class MonitorService{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JobExecuteEngine jobExecuteEngine;
	
    @Value("${admin.mobilenos}")
	private String adminMobileNos;
    
	@Autowired 
	private JobBoxStatusOptLogService jobBoxStatusOptLogService;
	
	@Scheduled(cron = "0 00 * * * ?")//每隔一小时检查一次
	public void checkOverTime(){
		List<JobBoxQueue> allQueues = jobExecuteEngine.getAllJobQueues();
		if(allQueues!=null) {
			for(JobBoxQueue jq:allQueues) {
				JobBox jb = jq.getHead();
				if(jb!=null) {
					try {
						check(jb);
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}
				}
			}
		}
	}
	
	private void check(JobBox jb) throws Exception{
		List<Task> tasks = jb.getTasksList();
		if(null!=tasks) {
			for(Task task:tasks) {
				if(TaskProcessStatus.STARTED.equals(task.getTaskProcessStatus())) {
					String startedTime = jobBoxStatusOptLogService.getStartedTime(task.getJobCode(), task.getEventTime(), task.getTaskCode());
					if(null!=startedTime) {
						DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
						LocalDateTime endTime = LocalDateTime.now();
						Duration duration = Duration.between(LocalDateTime.parse(startedTime,df),endTime);
						if(null!=task.getOverTimeMinute()) {
							if(duration.compareTo(Duration.ofMinutes(Long.parseLong(task.getOverTimeMinute())))>0) {
								String content = "Job:" + jb.getJobCode() + ",Task:" + task.getTaskCode() + " 已运行"
										+ duration.toMinutes() + "分钟，设定的预警值为 " + task.getOverTimeMinute() + "分钟";
								//messageService.smsAlert(jb.getJobCode(), content);
							}
						}
					}
				}
			}
		}
	}

}
