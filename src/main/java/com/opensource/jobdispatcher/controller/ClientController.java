package com.opensource.jobdispatcher.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/task")
@Api(value="查看task实现端的task详细信息",tags={"转发http请求"})
public class ClientController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Value("${taskinfo.url}")
	private String taskInfoUrl;
		
	@Autowired
	private RestTemplate restTemplate;

	@ApiOperation(value="查看任务基本信息", notes="来源于任务客户端系统的接口")
	@ApiImplicitParams(value = {  
        	@ApiImplicitParam(name = "taskcode",  value = "taskcode", required = true, paramType = "query",  dataType = "String")
    })
	@PostMapping(value = "/cons", produces = "application/json;charset=UTF-8")
	public String queryJob(@RequestParam("url") String url){
		String result ="TaskInfo API is down";
		try {
			result = restTemplate.getForObject(taskInfoUrl+url, String.class);
		}catch(Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}

	
	
}