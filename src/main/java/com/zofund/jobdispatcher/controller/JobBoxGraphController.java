package com.zofund.jobdispatcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zofund.jobdispatcher.controller.service.GraphControllerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/graph")
@Api(value="任务调度引擎监控和信息查看",tags={"查看JOB的处理情况,用于做图"})
public class JobBoxGraphController {
	
	@Autowired
	private GraphControllerService graphControllerService;
	
	@ApiOperation(value="JOB处理队列查看", notes="未完成的所有队列")
	@GetMapping(value = "/unfinishedqueues", produces = "application/json;charset=UTF-8")
	public String getWaitingQueue(){
		return graphControllerService.getWaitingQueue().toString();
	}
	
	@ApiOperation(value="JOB内部的Task处理情况", notes="job中的task的拓扑关系和处理状态")
	@GetMapping(value = "/tasksinjob", produces = "application/json;charset=UTF-8")
	public String tasksInJob(@RequestParam("jobcode") String jobCode,@RequestParam("eventtime") String eventTime){
		return graphControllerService.tasksInJob(jobCode,eventTime).toString();
	}
	
	@ApiOperation(value="查看quartzJob拓扑图", notes="查看quartzJob拓扑图")
	@GetMapping(value = "/quartzjob", produces = "application/json;charset=UTF-8")
	public String quartzjob(@RequestParam("jobcode") String jobCode){
		return graphControllerService.getQuartzJob(jobCode).toString();
	}
	
	@ApiOperation(value="任务执行的情况", notes="单个任务的执行时序")
	@GetMapping(value = "/task", produces = "application/json;charset=UTF-8")
	public String tasksInJob(@RequestParam("jobcode") String jobCode,@RequestParam("eventtime") String eventTime,
			@RequestParam("taskcode") String taskCode){
		return graphControllerService.calStatusOptLog(jobCode, eventTime, taskCode).toString();
	}


}