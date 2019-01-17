package com.opensource.jobdispatcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opensource.jobdispatcher.controller.service.JobBoxMonitorControllerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/monitor")
@Api(value="任务调度引擎监控和信息查看",tags={"查看JOB的处理情况"})
public class JobBoxMonitorController {
	
	@Autowired
	private JobBoxMonitorControllerService jobBoxMonitorControllerService;
	
	@ApiOperation(value="JOB处理队列查看", notes="未完成的所有队列")
	@GetMapping(value = "/processqueues", produces = "application/json;charset=UTF-8")
	public String getWaitingQueue(){
		return jobBoxMonitorControllerService.getWaitingQueue().toString();
	}
	
	@ApiOperation(value="JOB历史队列查看", notes="jobhistory")
	@ApiImplicitParams(value = {  
            	@ApiImplicitParam(name = "jobCode",   value = "可选，不填则查询全部", required = false, paramType = "query",  dataType = "String"),
	            @ApiImplicitParam(name = "startDate", value = "格式:20170101",    required = true, paramType = "query", dataType = "String"),
	            @ApiImplicitParam(name = "endDate",   value = "格式:20190901",    required = true, paramType = "query", dataType = "String")
	 }) 
	@GetMapping(value = "/jobhistory", produces = "application/json;charset=UTF-8")
	public String getJobHistory(String jobCode,@RequestParam("startDate")String startDate,@RequestParam("endDate")String endDate,
			@RequestParam("pageNum")Integer pageNum,@RequestParam("pageSize")Integer pageSize){
		    startDate = startDate.replaceAll("-", "")+"000000";
		    endDate = endDate.replaceAll("-", "")+"235999";
		    return jobBoxMonitorControllerService.getJobHistory(jobCode, startDate, endDate, pageNum, pageSize).toString();
	    }

}