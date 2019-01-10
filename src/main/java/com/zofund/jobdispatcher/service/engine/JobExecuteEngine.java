package com.zofund.jobdispatcher.service.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import com.zofund.jobdispatcher.model.middleware.JobBox;

@Service
public class JobExecuteEngine{
    
    private ConcurrentHashMap<String,JobBoxQueue> queuesMap = new ConcurrentHashMap<String,JobBoxQueue>();
    
	JobBoxQueue getJobQueueByCode(String jobCode){
		JobBoxQueue jobBoxQueue = queuesMap.get(jobCode);
		while(null == jobBoxQueue) {
			queuesMap.put(jobCode, new JobBoxQueue(jobCode));
			jobBoxQueue = queuesMap.get(jobCode);
		}
		return jobBoxQueue;
	}
	
	public Map<String,List<JobBox>> getWaitingQueueMap() {
		Map<String,List<JobBox>> queues = new HashMap<String,List<JobBox>>();
		List<JobBoxQueue> allQueues = getAllJobQueues();
		for(JobBoxQueue queue:allQueues) {
			queues.put(queue.getJobCode(), queue.getQueueArray());
		}
		return queues;
	}
	
	public List<JobBoxQueue> getAllJobQueues(){
		List<JobBoxQueue> queueList = new ArrayList<JobBoxQueue>();
		for(JobBoxQueue queue:queuesMap.values()) {
			queueList.add(queue);
		}
		return queueList;
	}

	public JobBox getJobIM(String jobCode,@NotNull String eventTime) {
		JobBoxQueue queue = getJobQueueByCode(jobCode);
		JobBox jb = null;
		if(queue!=null) {
			jb = queue.getJobBox(eventTime);
		}
		return jb;
	}
	
	public List<String> getWaitingTasksEventTimeList(String jobCode) {
		List<String> eventTimeList = new ArrayList<String>();
		JobBoxQueue jobQueue = getJobQueueByCode(jobCode);
		for(JobBox jb:jobQueue.getQueueArray()) {
			eventTimeList.add(jb.getEventTime());
		}
		return eventTimeList;
	}

}
