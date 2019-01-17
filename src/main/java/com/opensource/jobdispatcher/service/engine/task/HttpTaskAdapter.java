package com.opensource.jobdispatcher.service.engine.task;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opensource.jobdispatcher.dao.middleware.TaskStatusOptLogDao;
import com.opensource.jobdispatcher.model.middleware.TaskProcessStatus;
import com.opensource.jobdispatcher.service.engine.JobExecuteOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Service("httpTaskAdapter")
@RestController
@RequestMapping("/task")
@Api(value="任务调度引擎与任务执行者通信的适配器，此处是HTTP方式通信的适配器",tags={"提供接口供任务执行者修改任务执行的状态"})
public class HttpTaskAdapter{
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
	private JobExecuteOperator jobExecuteOperator;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private TaskStatusOptLogDao taskStatusOptLogDao;
	
	private String SEQ_TYPE = "TASK_EXECUTOR:";
	
	@ApiOperation(value="供任务执行者修改任务执行的状态", notes="回调接口")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "jobcode",  value = "jobcode", required = true, paramType = "query",  dataType = "String"),
        	@ApiImplicitParam(name = "taskcode",  value = "taskcode", required = true, paramType = "query",  dataType = "String"),
        	@ApiImplicitParam(name = "status",  value = "未开始, 0;正在执行, 1;需重新执行, 2;已暂停, 3;已完毕, 4;", required = true, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/call", produces = "application/json;charset=UTF-8")
	public String callStatus(@RequestParam("jobcode") String jobCode,@RequestParam("taskcode") String taskCode,
			@RequestParam("statusmap") String statusmap,@RequestParam("optseq") String optSeq) throws Exception {
		log.info("################jobCode is:"+jobCode+",taskCode is :"+taskCode+",statusmap:"+statusmap);
		JsonObject jo = new JsonParser().parse(statusmap).getAsJsonObject();
		JsonArray resultsMap = new JsonArray();
		if(null!=jo) {
			for(String eventTime:jo.keySet()) {
				TaskProcessStatus status = TaskProcessStatus.getTaskProcessStatus(jo.get(eventTime).getAsInt());
				Boolean updateResult = jobExecuteOperator.updateTaskStatus(jobCode, taskCode,eventTime,status,SEQ_TYPE+optSeq);
				JsonObject j = new JsonObject();
				j.addProperty("jobCode",   jobCode);
				j.addProperty("taskCode",  taskCode);
				j.addProperty("eventTime", eventTime);
				j.addProperty("statusInt", jo.get(eventTime).getAsInt());
				j.addProperty("flag", updateResult?"succ":"fail");
				resultsMap.add(j);
			}
		}
		return resultsMap.toString();
	}

	public Boolean triger(String jobCode,String taskCode,ArrayList<String> eventTimeList,String httpUrl){
		HttpHeaders headers = new HttpHeaders();
	    MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");  
	    headers.setContentType(type);
	    JsonObject bodyJson = new JsonObject();
	    bodyJson.addProperty("jobCode", jobCode);
	    bodyJson.addProperty("taskCode", taskCode);
	    String optSeq = taskStatusOptLogDao.getOptSeq();
	    bodyJson.addProperty("optSeq", optSeq);
	    if(null!=eventTimeList) {
	    	JsonArray ja = new JsonArray();
	    	for(String eventTime:eventTimeList) {
				ja.add(eventTime);
	    	}
		    bodyJson.add("eventTimeList",ja);
	    }
	    HttpEntity<String> requestEntity = new HttpEntity<String>(bodyJson.toString(), headers);
	    log.info("################Body is:"+bodyJson.toString());
	    jobExecuteOperator.updateTaskStatus(jobCode, taskCode,eventTimeList.get(0), TaskProcessStatus.TRIGGERED,SEQ_TYPE+optSeq);
	    String triggerFlag = getTriggerFlag(httpUrl,requestEntity);
	    log.info("################触发标记为"+triggerFlag);
	    if("0000".equals(triggerFlag)){
	    	jobExecuteOperator.updateTaskStatus(jobCode, taskCode,eventTimeList.get(0), TaskProcessStatus.STARTED,SEQ_TYPE+optSeq);
	    	return true;
	    }else if("9999".equals(triggerFlag)){
	    	jobExecuteOperator.updateTaskStatus(jobCode, taskCode,eventTimeList.get(0),TaskProcessStatus.PAUSED,SEQ_TYPE+optSeq);
	    	return true;
	    }
	    jobExecuteOperator.updateTaskStatus(jobCode,taskCode,eventTimeList.get(0),
	    										TaskProcessStatus.NEED_RESTART,SEQ_TYPE+optSeq+",Retry");
	    return false;
	}

	private String getTriggerFlag(String httpUrl, HttpEntity<String> requestEntity){
		try{
			return restTemplate.postForObject(httpUrl,requestEntity,String.class);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
}
