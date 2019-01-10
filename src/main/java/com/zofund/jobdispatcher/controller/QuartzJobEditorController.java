package com.zofund.jobdispatcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.zofund.jobdispatcher.controller.service.GraphControllerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/editor")
@Api(value="QuartzJob编辑器",tags={"QuartzJob编辑器"})
public class QuartzJobEditorController {
	
	@Autowired
	private GraphControllerService graphControllerService;
	
	@ApiOperation(value="预览QuartzJob", notes="POST接口")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "json",  value = "json格式的QuartzJob配置文件", required = true, paramType = "query",  dataType = "String")
    })
	@PostMapping(value = "/preview", produces = "application/json;charset=UTF-8")
	public String previewQuartzJob(@RequestBody String json){
		return previewJobFromJson(json);
	}
	
	@ApiOperation(value="预览QuartzJob", notes="GET接口")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "json",  value = "json格式的QuartzJob配置文件", required = true, paramType = "query",  dataType = "String")
    })
	@GetMapping(value = "/preview", produces = "application/json;charset=UTF-8")
	public String previewJobFromJson(@RequestParam("json") String json){
		return graphControllerService.previewQuartzJob(json).toString();
	}
	
}