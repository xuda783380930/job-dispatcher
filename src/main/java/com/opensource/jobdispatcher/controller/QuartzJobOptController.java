package com.opensource.jobdispatcher.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opensource.jobdispatcher.controller.service.QuartzJobControllerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/quartz")
@Api(value="任务调度引擎quartz-API",tags={"注册QuartzJob"})
public class QuartzJobOptController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private QuartzJobControllerService quartzJobControllerService;
	
	@ApiOperation(value="注册QuartzJob", notes="POST接口")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "json",  value = "json格式的QuartzJob配置文件", required = true, paramType = "query",  dataType = "String")
    })
	@PostMapping(value = "/registerjob", produces = "application/json;charset=UTF-8")
	public String registerQuartzJob(@RequestBody String json){
		return registerJobFromJson(json);
	}
	
	@ApiOperation(value="注册QuartzJob", notes="GET接口")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "json",  value = "json格式的QuartzJob配置文件", required = true, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/registerjob", produces = "application/json;charset=UTF-8")
	public String registerJobFromJson(@RequestParam("json") String json){
		log.info("<<<<<<<< Get quartzJob 配置文件:"+json);
		return quartzJobControllerService.registerOneQuartzJob(json).toString();
	}
	
	@ApiOperation(value="删除QuartzJob", notes="GET接口")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "jobcode",  value = "jobCode", required = true, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/removejob", produces = "application/json;charset=UTF-8")
	public String removeJobFromJson(@RequestParam("jobcode") String jobCode){
		return quartzJobControllerService.removeOneQuartzJob(jobCode).toString();
	}
	
	@ApiOperation(value="查询所有的QuartzJob", notes="可通过JobCode查询,不加JobCode则查询所有")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "jobCode",  value = "jobCode", required = false, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/jobs", produces = "application/json;charset=UTF-8")
	public String queryQuartzJob(String jobCode){
		return quartzJobControllerService.queryQuartzJob(jobCode).toString();
	}

}