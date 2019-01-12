package com.zofund.jobdispatcher.controller.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zofund.jobdispatcher.model.middleware.JobBox;
import com.zofund.jobdispatcher.model.middleware.Task;
import com.zofund.jobdispatcher.model.middleware.TaskProcessStatus;
import com.zofund.jobdispatcher.model.middleware.TaskStatusOptLog;
import com.zofund.jobdispatcher.model.middleware.builder.JobBoxBuilder;
import com.zofund.jobdispatcher.model.middleware.builder.TaskBuilder;
import com.zofund.jobdispatcher.model.middleware.vo.JobStructVO;
import com.zofund.jobdispatcher.service.JobBoxBuilderRegisterCenter;
import com.zofund.jobdispatcher.service.JobBoxService;
import com.zofund.jobdispatcher.service.db.JobBoxStatusOptLogService;
import com.zofund.jobdispatcher.service.engine.JobExecuteEngine;

@Service
public class GraphControllerService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired 
	private JobBoxStatusOptLogService jobBoxStatusOptLogService;
	
	@Autowired
	private JobExecuteEngine  jobExecuteEngine;
	
	@Autowired
	private QuartzJobControllerService quartzJobControllerService;
	
	@Autowired
	private JobBoxBuilderRegisterCenter jobBoxBuilderRegisterCenter;
	
	@Autowired
	private JobBoxService jobBoxService; 

	public void calXY(JobBoxBuilder jbb,JsonArray postionArray,JsonArray linksArray) {
	    HashMap<Integer, HashMap<Integer, JsonObject>> positionMap = new HashMap<Integer,HashMap<Integer,JsonObject>>();
		calDownstreamXY(jbb,null,0,0,positionMap,new HashSet<String>(),linksArray);
		
	    Integer xSum = 1600;
	    Integer ySum = 800;
		cacXYSize(xSum,ySum,positionMap,postionArray);
	}
	
	public void calXY(JobBox jobBox,JsonArray postionArray,JsonArray linksArray) {
	    HashMap<Integer, HashMap<Integer, JsonObject>> positionMap = new HashMap<Integer,HashMap<Integer,JsonObject>>();
		calDownstreamXY(jobBox,null,0,0,positionMap,new HashSet<String>(),linksArray);
	    Integer xSum = 1600;
	    Integer ySum = 800;
		cacXYSize(xSum,ySum,positionMap,postionArray);
	}
	
	private void cacXYSize(Integer xSum,Integer ySum,HashMap<Integer, HashMap<Integer, JsonObject>> positionMap,JsonArray postionArray) {
		Integer maxX = 1;
		Integer maxY = 1;
		for(HashMap<Integer, JsonObject> hx:positionMap.values()) {
		    	for(JsonObject jo:hx.values()) {
		    		Integer  x = jo.get("x").getAsInt();
		    		Integer  y = jo.get("y").getAsInt();
		    		if(x.compareTo(maxX)>0) {
		    			maxX = x;
		    		}
		    		if(y.compareTo(maxY)>0) {
		    			maxY = y;
		    		}
		    	}
		}
	    for(HashMap<Integer, JsonObject> hx:positionMap.values()) {
	    	for(JsonObject jo:hx.values()) {
		       Integer  x = jo.get("x").getAsInt();
		       Integer  y = jo.get("y").getAsInt();
		       jo.addProperty("x", 1==maxX?0:(xSum*(x-1)/(maxX-1)));
		       jo.addProperty("y", 1==maxY?0:(ySum*(y-1)/(maxY-1)));
	 		   postionArray.add(jo);
	    	}
	   }
		
	}

	private void calDownstreamXY(JobBoxBuilder jbb,TaskBuilder nowTask,Integer x,Integer y,
			HashMap<Integer,HashMap<Integer,JsonObject>> positionMap,HashSet<String> taskCodes,JsonArray linksArray) {
		JobStructVO js = jbb.getJobStructVO();
		List<String> downStreamTasks = js.getDownstreamTasksSortByDeep(nowTask==null?null:nowTask.getTaskCode());
		if(null!=downStreamTasks) {
			for(String taskCode:downStreamTasks) {
				Integer deepx = x + 1;
				Integer deepy = y;
				TaskBuilder task = jbb.getTaskBuilderByTaskCode(taskCode);
				if(!taskCodes.contains(taskCode)) {
					JsonObject jo = new JsonObject();
					jo.addProperty("name", taskCode);
					jo.addProperty("x", deepx);
					if(null!=positionMap.get(deepx)) {
						deepy = 1 + positionMap.get(deepx).size();
					}else {
						deepy = 1;
					}
					jo.addProperty("y", deepy);
					
					jo.addProperty("taskUrl", task.getAdapterPara());
					if(task.getOverTimeMinute()!=null) {
						jo.addProperty("overTime", task.getOverTimeMinute());
					}
					if(null==positionMap.get(deepx)) {
						positionMap.put(deepx, new HashMap<Integer,JsonObject>());
					}
					positionMap.get(deepx).put(deepy, jo);
					taskCodes.add(task.getTaskCode());
				}

				if(null!=nowTask) {
					JsonObject link = new JsonObject();
					link.addProperty("source", nowTask.getTaskCode());
					link.addProperty("target", task.getTaskCode());
					linksArray.add(link);
				}
				calDownstreamXY(jbb,task,deepx,deepy,positionMap,taskCodes,linksArray);
			}
		}
			
	}
	
	private void calDownstreamXY(JobBox jobBox,Task nowTask,Integer x,Integer y,
			HashMap<Integer,HashMap<Integer,JsonObject>> positionMap,HashSet<String> taskCodes,JsonArray linksArray) {
		String nowTaskCode = null;
		if(null != nowTask) {
			nowTaskCode = nowTask.getTaskCode();
		}
		List<String> downStreamTasks = jobBox.getJobStructVO().getDownstreamTasksSortByDeep(nowTaskCode);
		if(null!=downStreamTasks) {
			for(String downStreamTaskCode:downStreamTasks) {
				Integer deepx = x+1;
				Integer deepy = y;
				Task task = jobBox.getTask(downStreamTaskCode);
				if(!taskCodes.contains(downStreamTaskCode)) {
					JsonObject jo = new JsonObject();
					jo.addProperty("name", downStreamTaskCode);
					jo.addProperty("x", deepx);
					if(null!=positionMap.get(deepx)) {
						deepy = 1 + positionMap.get(deepx).size();
					}else {
						deepy = 1;
					}
					jo.addProperty("y", deepy);
					String labelStr =  "";
					TaskProcessStatus taskStatus = task.getTaskProcessStatus();
					if(!TaskProcessStatus.UNSTART.equals(taskStatus)) {
						TaskStatusOptLog recentStatusOpt = jobBoxStatusOptLogService.getStatusOptTime(task.getJobCode(), task.getEventTime(), task.getTaskCode(),
								task.getTaskProcessStatus());
						if(null!=recentStatusOpt) {
							String recentStatusOptTime = recentStatusOpt.getOptTime();
							if(null!=recentStatusOptTime) {
								String optSeq = recentStatusOpt.getOptSeq();
								labelStr = labelStr + formatTimeStr(recentStatusOptTime.toString()) + " " +
										(optSeq==null?"":optSeq.toString()) + "将状态修改为" + taskStatus.getName();
								String startedTime = jobBoxStatusOptLogService.getStartedTime(task.getJobCode(), task.getEventTime(), task.getTaskCode());
								if(null!=startedTime) {
									DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
									LocalDateTime endTime = LocalDateTime.parse(recentStatusOptTime.toString(),df);
									if(TaskProcessStatus.STARTED.equals(taskStatus)) {
										endTime = LocalDateTime.now();
									}
									Duration duration = Duration.between( LocalDateTime.parse(startedTime,df),endTime);
									labelStr = labelStr + " \n " + "共耗时:" + duration;
								}
							}
						}
					}else {
						labelStr = labelStr + taskStatus.getName();
					}
					jo.addProperty("label", labelStr);
					jo.addProperty("color", getNodeColor(taskStatus));
					jo.addProperty("status", taskStatus.getName());
					if(null==positionMap.get(deepx)) {
						positionMap.put(deepx, new HashMap<Integer,JsonObject>());
					}
					positionMap.get(deepx).put(deepy, jo);
					taskCodes.add(task.getTaskCode());
				}

				if(null!=nowTask) {
					JsonObject link = new JsonObject();
					link.addProperty("source", nowTask.getTaskCode());
					link.addProperty("target", task.getTaskCode());
					linksArray.add(link);
				}
				calDownstreamXY(jobBox,task,deepx,deepy,positionMap,taskCodes,linksArray);
			}
		}
			
	}

	public String getNodeColor(TaskProcessStatus status) {
		switch(status.toString()){  
        case "UNSTART":
        	return "#9675ce";//紫色
        case "STARTED":  
        	return "#87CEFA";//淡蓝色
        case "NEED_RESTART":  
        	return "#F37B1D";//橘黄色
        case "PAUSED":
        	return "#FF0000";//红色
        case "FINISHED":
        	return "#008000";//绿色
        case "TRIGGERED":
        	return "#EE7AE9";
       }
		return "#000000";//永远不可能出现
	}
	
	
	private String formatTimeStr(String time) {
		return time.substring(0, 4)+"-"+time.substring(4, 6)+"-"+time.substring(6, 8)+
			   " "+time.substring(8,10)+":"+time.substring(10,12)+":"+time.substring(12,14);
	}

	public JsonObject getWaitingQueue() {
		JsonObject resultJson = new JsonObject();
		
		JsonArray allJobs = new JsonArray();
		JsonArray allCategorysArr = new JsonArray();
		JsonArray linksArray = new JsonArray();
		HashSet<String> allCategorys= new HashSet<String>();
		
		int j = 0 ;
		Map<String, List<JobBox>> queuesMap = jobExecuteEngine.getWaitingQueueMap();
		for(String jobCode:queuesMap.keySet()) {
			allCategorys.add(jobCode);
			List<JobBox> queue = queuesMap.get(jobCode);
			j = j + queue.size();
			for(int i=0;i< queue.size();i++){
				JobBox jb = (JobBox) queue.get(i);
				JsonArray jaa = new JsonArray();
				jaa.add(jb.getJobCode());
				jaa.add(jb.getEventTime());
				String jobStr = jb.getJobStatus().getName();
				if(0 == i ) {
					jaa.add("red");
					jobStr = "队首触发中:" + jobStr;
				}else {
					jaa.add("green");
					jobStr = "队列中未触发:" + jobStr;
				}
				jaa.add(jobStr);
				allJobs.add(jaa);
				
				if(allJobs.size()!=j) {
					JsonObject link = new JsonObject();
					link.addProperty("source", allJobs.size()-1);
					link.addProperty("target", allJobs.size());
					linksArray.add(link);
				}
			}
		}
		
		for(String category:allCategorys) {
			allCategorysArr.add(category);
		}
		
		resultJson.add("nodes",allJobs);
		resultJson.add("links", linksArray);
		resultJson.add("axisData",allCategorysArr);
		return resultJson;
		
	}

	public JsonObject tasksInJob(String jobCode, String eventTime) {
		JsonObject joo = new JsonObject();
		JobBox jb = jobExecuteEngine.getJobIM(jobCode,eventTime);
		if(null==jb) {
			jb = jobBoxService.getJob(jobCode, eventTime);
		}
		if(null!=jb) {
			JsonArray postionArray = new JsonArray();
			JsonArray linksArray = new JsonArray();
			calXY(jb,postionArray,linksArray);
			joo.add("nodes", postionArray);
			joo.add("links", linksArray);
		}
		return joo;
	}

	public JsonObject calStatusOptLog(String jobCode, String eventTime, String taskCode) {
		JsonObject resultJson = new JsonObject();
		JsonArray taskOptLog = new JsonArray();
		JsonArray allCategorysArr = new JsonArray();
		allCategorysArr.add(TaskProcessStatus.UNSTART.getName());
		allCategorysArr.add(TaskProcessStatus.TRIGGERED.getName());
		allCategorysArr.add(TaskProcessStatus.NEED_RESTART.getName());
		allCategorysArr.add(TaskProcessStatus.STARTED.getName());
		allCategorysArr.add(TaskProcessStatus.PAUSED.getName());
		allCategorysArr.add(TaskProcessStatus.FINISHED.getName());
		
		JsonArray linksArray = new JsonArray();
		
		JsonArray jaa = new JsonArray();
		jaa.add(TaskProcessStatus.UNSTART.getName());
		jaa.add(eventTime);
		jaa.add(getNodeColor(TaskProcessStatus.UNSTART));
		taskOptLog.add(jaa);
		
		List<TaskStatusOptLog> l = jobBoxStatusOptLogService.getStatusOptLog(jobCode, eventTime, taskCode);
		if(null!=l) {
			for(TaskStatusOptLog log:l) {
				jaa = new JsonArray();
				TaskProcessStatus afterStatus = log.getAfterStatus();
				jaa.add(afterStatus.getName());
				jaa.add(log.getOptTime());
				jaa.add(getNodeColor(afterStatus));
				jaa.add(log.getOptSeq());
				taskOptLog.add(jaa);
					
				JsonObject link = new JsonObject();
				link.addProperty("source", taskOptLog.size()-2);
				link.addProperty("target", taskOptLog.size()-1);
				linksArray.add(link);
			}
		}
		
		resultJson.add("nodes",taskOptLog);
		resultJson.add("links", linksArray);
		resultJson.add("axisData",allCategorysArr);
		return resultJson;
	}

	public JsonObject getQuartzJob(String jobCode) {
		JsonObject joo = new JsonObject();
		JobBoxBuilder jbb = jobBoxBuilderRegisterCenter.getJobBoxBuilderByJobCode(jobCode);
		if(null!=jbb) {
			JsonArray postionArray = new JsonArray();
			JsonArray linksArray = new JsonArray();
			calXY(jbb,postionArray,linksArray);
			joo.add("nodes", postionArray);
			joo.add("links", linksArray);
		}
		return joo;
	}

	public JsonObject previewQuartzJob(String json) {
		JsonObject joo = new JsonObject();
		log.info("<<<<<<<< Get quartzJob 配置文件:"+json);
		try{
			JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
			JsonObject jbBuilderJO = jo.get("jobBoxBuilder").getAsJsonObject();
		    JobBoxBuilder jbb = quartzJobControllerService.jo2JobBoxBuilder(jbBuilderJO);
	
		    JsonArray postionArray = new JsonArray();
		    JsonArray linksArray = new JsonArray();
		    if(null!=jbb.getJobStructVO().getDuplicatedLinkNodes(null)) {
			    joo.addProperty("duplicateNodes", jbb.getJobStructVO().getDuplicatedLinkNodes(null).toString());
		    }
		    calXY(jbb,postionArray,linksArray);
			joo.add("nodes", postionArray);
			joo.add("links", linksArray);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return joo;
	}




}