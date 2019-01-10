package com.zofund.jobdispatcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.zofund.jobdispatcher.controller.service.JobBoxOptControllerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/jobbox")
@Api(value="对Job进行状态操作",tags={"对Job或job中的task节点进行干预操作"})
public class JobBoxOptController {
	
	@Autowired
	private JobBoxOptControllerService jobBoxOptControllerService;

	@ApiOperation(value="暂停一个Job", notes="如果一个task正在运行中,则该task的下游task会被暂停")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "jobcode",  value = "jobcode", required = true, paramType = "query",  dataType = "String"),
        	@ApiImplicitParam(name = "eventtime",value = "eventtime", required = true, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/stopjob", produces = "application/json;charset=UTF-8")
	public String stopJob(@RequestParam("jobcode") String jobCode,@RequestParam("eventtime") String eventTime){
		return jobBoxOptControllerService.stopJob(jobCode, eventTime).toString();
	}

	@ApiOperation(value="启动一个暂停的Job", notes="job中所有的暂停的Task会被重设为NEED_RESTART")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "jobcode",  value = "jobcode", required = true, paramType = "query",  dataType = "String"),
        	@ApiImplicitParam(name = "eventtime",value = "eventtime", required = true, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/startjob", produces = "application/json;charset=UTF-8")
	public String startJob(@RequestParam("jobcode") String jobCode,@RequestParam("eventtime") String eventTime){
		return jobBoxOptControllerService.startJob(jobCode, eventTime).toString();
	}
	
	@ApiOperation(value="修改任务执行的状态", notes="人工设置")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "jobcode",  value = "jobcode", required = true, paramType = "query",  dataType = "String"),
        	@ApiImplicitParam(name = "taskcode",  value = "taskcode", required = true, paramType = "query",  dataType = "String"),
        	@ApiImplicitParam(name = "status",  value = "未开始, 0;正在执行, 1;需重新执行, 2;已暂停, 3;已完毕, 4;", required = true, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/updatestatus", produces = "application/json;charset=UTF-8")
	public String callStatus(@RequestParam("jobcode") String jobCode,@RequestParam("taskcode") String taskCode,
			@RequestParam("eventtime") String eventTime,
			@RequestParam("status") Integer status){
		return jobBoxOptControllerService.callStatus(jobCode, taskCode, eventTime, status).toString();
	}
	
	@ApiOperation(value="暂停一个Job队列", notes="即无法从队列中再取出新的Job，已开始的Job会继续运行")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "jobcode",  value = "jobcode", required = true, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/stopqueue", produces = "application/json;charset=UTF-8")
	public String stopQueue(@RequestParam("jobcode") String jobCode){
		return jobBoxOptControllerService.stopQueue(jobCode).toString();
	}
	
	@ApiOperation(value="启动一个暂停的Job队列", notes="可以从队列中再取出新的Job")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "jobcode",  value = "jobcode", required = true, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/startqueue", produces = "application/json;charset=UTF-8")
	public String startQueue(@RequestParam("jobcode") String jobCode){
		return jobBoxOptControllerService.startQueue(jobCode).toString();
	}

	
}