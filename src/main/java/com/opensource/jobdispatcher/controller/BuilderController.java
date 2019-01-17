package com.opensource.jobdispatcher.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.opensource.jobdispatcher.controller.service.BuilderControllerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/builder")
@Api(value="任务调度引擎quartz-API",tags={"builder相关"})
public class BuilderController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BuilderControllerService builderControllerService;
	
	@ApiOperation(value="查询所有的QuartzJob", notes="可通过JobCode查询,不加JobCode则查询所有")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "jobCode",  value = "jobCode", required = false, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/jobbuilders", produces = "application/json;charset=UTF-8")
	public String getAllJobBuilders(){
		return builderControllerService. getAllJobBuilders().toString();
	}

}