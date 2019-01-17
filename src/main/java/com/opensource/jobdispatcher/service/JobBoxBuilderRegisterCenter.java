package com.opensource.jobdispatcher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opensource.jobdispatcher.dao.middleware.JobBoxBuilderDao;
import com.opensource.jobdispatcher.model.middleware.builder.JobBoxBuilder;
import com.opensource.jobdispatcher.model.middleware.builder.TaskBuilder;

@Service
public class JobBoxBuilderRegisterCenter{
	
	@Autowired
	private  JobBoxBuilderDao jobBoxBuilderDao;
	
    public String registerJobBoxBuilder(JobBoxBuilder jb){
    	String errMsg = null;
    	for(TaskBuilder tb:jb.getTaskBuilderList()) {
    		List<String> upstreamTaskCodes = tb.getUpstreamTaskCodes();
    		for(String upstreamTaskCode:upstreamTaskCodes) {
                 if(null==jb.getTaskBuilderByTaskCode(upstreamTaskCode)) {
                	  errMsg = "###" + tb.getTaskCode() + " upstreamTaskCode:" + upstreamTaskCode + " is missing!";
                	  return errMsg;
                  }
    		}
    	}
    	if(null!=jb.getJobStructVO().getDuplicatedLinkNodes(null)){
    		errMsg = "###" + jb.getJobStructVO().getDuplicatedLinkNodes(null).toString() + " is Duplicated!";
     	    return errMsg;
    	}
    	jobBoxBuilderDao.deleteById(jb.getJobCode());
    	jobBoxBuilderDao.save(jb);
    	return null;
    }
    
	public void unregisterJobBoxBuilder(String jobCode) {
		jobBoxBuilderDao.deleteById(jobCode);
	}

	public JobBoxBuilder getJobBoxBuilderByJobCode(String jobCode) {
		return jobBoxBuilderDao.findById(jobCode).get();
	}
	
	public List<JobBoxBuilder> getAllJobBoxBuilders() {
		return jobBoxBuilderDao.findAll();
	}
}
