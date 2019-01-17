package com.opensource.jobdispatcher.controller.service;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opensource.jobdispatcher.model.middleware.builder.JobBoxBuilder;
import com.opensource.jobdispatcher.model.middleware.builder.TaskBuilder;
import com.opensource.jobdispatcher.model.middleware.quartz.QuartzJob;
import com.opensource.jobdispatcher.service.JobBoxBuilderRegisterCenter;
import com.opensource.jobdispatcher.service.quartz.QuartzService;

@Service
public class QuartzJobControllerService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private QuartzService  quartzService;
	
	@Autowired
	private JobBoxBuilderRegisterCenter jobBoxBuilderRegisterCenter;
	 
	private String HTTP_ADAPTERSPRINGID = "httpTaskAdapter";
	
	private String HTTP_ADAPTERMETHODNAME = "triger";
	
	private String registerQuartzJob(@NotNull String json){
		JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
		JsonObject jbBuilderJO = jo.get("jobBoxBuilder").getAsJsonObject();
		String jobCode = jbBuilderJO.get("jobCode").getAsString();
		JobBoxBuilder jb = jo2JobBoxBuilder(jbBuilderJO);
		
		String jobCronExpression = jo.get("jobCronExpression").getAsString();
		String filterCalendarName = jo.get("filterCalendarName").getAsString();
    	QuartzJob quartzJob = new QuartzJob();
    	quartzJob.setJobCode(jobCode);
    	quartzJob.setFilterCalendarName(filterCalendarName);
    	quartzJob.setJobCronExpression(jobCronExpression);
    	
		return registerOneQuartzJob(quartzJob, jb);
	}

	public JobBoxBuilder jo2JobBoxBuilder(JsonObject jbBuilderJO) {
		String jobCode = jbBuilderJO.get("jobCode").getAsString();
		List<TaskBuilder> taskBuilderList = new ArrayList<TaskBuilder>();
		for(JsonElement taskJE:jbBuilderJO.get("taskBuilderMap").getAsJsonArray()) {
			TaskBuilder t = new TaskBuilder();
			JsonObject taskJson = taskJE.getAsJsonObject();
			t.setJobCode(jobCode);
			t.setTaskCode(taskJson.get("taskCode").getAsString());
			if(null!=taskJson.get("overTime")) {
				t.setOverTimeMinute(Long.valueOf(taskJson.get("overTime").getAsString())); 
			}
			t.setAdapterSpringId(HTTP_ADAPTERSPRINGID);
			t.setAdapterMethodName(HTTP_ADAPTERMETHODNAME);
			t.setAdapterPara(taskJson.get("adapterPara").getAsString());
			if(null!=taskJson.get("upstreamTaskCodes")) {
				StringBuilder upstreamTaskCodes = new StringBuilder();
				for(JsonElement upStreamTaskJE:taskJson.get("upstreamTaskCodes").getAsJsonArray()) {
					upstreamTaskCodes.append(upStreamTaskJE.getAsString()).append(",");
				}
				if(upstreamTaskCodes.length()>0) {
					String ut = upstreamTaskCodes.substring(0,upstreamTaskCodes.length()-1);
					t.setUpstreamTaskCodes(ut);
				}
			}
			taskBuilderList.add(t);
		}

		JsonElement mPObj = jbBuilderJO.get("monitorParas");
		String monitorParas = null== mPObj?null:mPObj.getAsString();
		JobBoxBuilder jb = new JobBoxBuilder();
		jb.setJobCode(jobCode);
		jb.setMonitorParas(monitorParas);
		jb.setTaskBuilderList(taskBuilderList);
		return jb;
	}

	public JsonObject registerOneQuartzJob(String json) {
		JsonObject result = new JsonObject();
		String errMsg = registerQuartzJob(json);
		if(null==errMsg){
			result.addProperty("flag", "succ");
		}else {
	        result.addProperty("flag", "fail");
	        result.addProperty("errMsg", errMsg);
		}
        return result;
	}

	public JsonObject removeOneQuartzJob(String jobCode) {
		JsonObject result = new JsonObject();
		if(unregisterQuartzJob(jobCode)) {
			result.addProperty("flag", "succ");
		}else {
			result.addProperty("flag", "fail");
		}
		return result;
	}

	public JsonArray queryQuartzJob(String jobCode) {
		List<QuartzJob> jobs = quartzService.getQuartzJobsByCode(jobCode);
		if(null==jobCode) {
			jobs = quartzService.getAllQuartzJobs();
		}
		JsonArray result = new JsonArray();
		if(null!=jobs) {
			for(QuartzJob qj:jobs) {
				result.add(new JsonParser().parse(new Gson().toJson(qj)).getAsJsonObject());
			}
		}
		return result;
	}
	
    public String registerOneQuartzJob(QuartzJob quartzJob,JobBoxBuilder jb) {
    	String errMsg = jobBoxBuilderRegisterCenter.registerJobBoxBuilder(jb);
    	if(null!=errMsg){
    		return errMsg;
    	}	
    	if(quartzService.registerQuartz(quartzJob)) {
    		return null;
    	}
 	    return "Fail in Quartz Scheduler!";
    }
    
    public Boolean unregisterQuartzJob(String jobCode){
    	jobBoxBuilderRegisterCenter.unregisterJobBoxBuilder(jobCode);
        if(!quartzService.delQuartzJob(jobCode)) {
        	return false;
        }
        return quartzService.removeQuartzJobIm(jobCode);
    }

}