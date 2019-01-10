package com.zofund.jobdispatcher.controller.service;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zofund.jobdispatcher.model.middleware.JobBox;
import com.zofund.jobdispatcher.model.middleware.Task;
import com.zofund.jobdispatcher.service.JobBoxService;
import com.zofund.jobdispatcher.service.engine.JobExecuteEngine;

@Service
public class JobBoxMonitorControllerService {
	
	@Autowired
	private JobExecuteEngine jobExecuteEngine;
	
	@Autowired
	private JobBoxService jobBoxService; 
	
    private JsonObject jobToJsonObject(JobBox jobBox){
 		JsonObject jobJson = new JsonObject();
 		jobJson.addProperty("jobCode", jobBox.getJobCode());
 		jobJson.addProperty("eventTime", jobBox.getEventTime().toString());

 		JsonObject tasksJson = new JsonObject();
 		for(Task task:jobBox.getTasksList()) {
 			tasksJson.add(task.getTaskCode(), task.toJsonObject());
 		}
 		jobJson.add("taskMap", tasksJson);
	 	jobJson.addProperty("jobStatus", jobBox.getJobStatus().toString());
 	
     	return jobJson;
     }
	
	public  JsonObject getWaitingQueue(){
		Map<String, List<JobBox>> map = jobExecuteEngine.getWaitingQueueMap();
		
		JsonObject queuesJson = new JsonObject();
		
		for(String jobCode:map.keySet()) {
			JsonArray queueJsonArray = null;
			if(null==queuesJson.get(jobCode)) {
				queuesJson.add(jobCode, new JsonArray());
			}
			queueJsonArray = queuesJson.get(jobCode).getAsJsonArray();
			List<JobBox> queue = map.get(jobCode);
			for(JobBox jb:queue){
				queueJsonArray.add(jobToJsonObject(jb));
			}
		}
		return queuesJson;
	}


	private Logger log = LoggerFactory.getLogger(this.getClass());
	

	
	public JsonArray getJobHistory(String jobCode,String startDate,String endDate,Integer pageNum,Integer pageSize){
		    startDate = startDate.replaceAll("-", "");
		    endDate = endDate.replaceAll("-", "");
		    List<JobBox> allJobs = jobBoxService.getJobList(jobCode,startDate,endDate,pageNum,pageSize);
	    	JsonArray allJobJsonArr = new JsonArray();
		    log.info("########## " + startDate);
		    log.info("########## " + endDate);
		    log.info("########## " + allJobs.size());
	    	if(null!= allJobs) {
	    		for(JobBox jobBox:allJobs) {
	    			allJobJsonArr.add(new JsonParser().parse(new Gson().toJson(jobBox)).getAsJsonObject());
	    		}
	    	}
	    	return allJobJsonArr;
	    }
	

}