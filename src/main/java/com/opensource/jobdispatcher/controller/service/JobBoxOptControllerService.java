package com.opensource.jobdispatcher.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.JsonObject;
import com.opensource.jobdispatcher.model.middleware.TaskProcessStatus;
import com.opensource.jobdispatcher.service.engine.JobExecuteOperator;

@Service
public class JobBoxOptControllerService {
	
	@Autowired
	private JobExecuteOperator jobExecuteOperator;

	public JsonObject stopJob(String jobCode,String eventTime){
		JsonObject result = new JsonObject();
		if(jobExecuteOperator.stopJob(jobCode,eventTime,"human")) {
			//通知处理池任务处理情况
			result.addProperty("flag", "succ");
			return result;
		}
		result.addProperty("flag", "fail");
		return result;
	}

	public JsonObject startJob(String jobCode,String eventTime){
		JsonObject result = new JsonObject();
		if(jobExecuteOperator.startJob(jobCode,eventTime,"human")) {
			//通知处理池任务处理情况
			result.addProperty("flag", "succ");
			return result;
		}
		result.addProperty("flag", "fail");
		return result;
	}
	
	public JsonObject callStatus(String jobCode,String taskCode,String eventTime,Integer statusInt){
		TaskProcessStatus status = TaskProcessStatus.getTaskProcessStatus(statusInt);
		Boolean updateResult = jobExecuteOperator.updateTaskStatus(jobCode, taskCode,eventTime,status,"manual");
		JsonObject j = new JsonObject();
		j.addProperty("jobCode",   jobCode);
		j.addProperty("taskCode",  taskCode);
		j.addProperty("eventTime", eventTime);
		j.addProperty("statusInt", statusInt);
		j.addProperty("flag", updateResult?"succ":"fail");
		return j;
	}
	
	public JsonObject stopQueue(String jobCode){
		JsonObject result = new JsonObject();
		if(jobExecuteOperator.stopQueue(jobCode)) {
			//通知处理池任务处理情况
			result.addProperty("flag", "succ");
			return result;
		}
		result.addProperty("flag", "fail");
		return result;
	}
	
	public JsonObject startQueue(String jobCode){
		JsonObject result = new JsonObject();
		if(jobExecuteOperator.startQueue(jobCode)) {
			//通知处理池任务处理情况
			result.addProperty("flag", "succ");
			return result;
		}
		result.addProperty("flag", "fail");
		return result;
	}

	
}