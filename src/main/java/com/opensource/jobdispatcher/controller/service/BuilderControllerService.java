package com.opensource.jobdispatcher.controller.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.opensource.jobdispatcher.model.middleware.builder.JobBoxBuilder;
import com.opensource.jobdispatcher.service.JobBoxBuilderRegisterCenter;

@Service
public class BuilderControllerService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	
	@Autowired
	private JobBoxBuilderRegisterCenter jobBoxBuilderRegisterCenter;

	public JsonArray getAllJobBuilders() {
		JsonArray ja = new JsonArray();
		List<JobBoxBuilder> jbs = jobBoxBuilderRegisterCenter.getAllJobBoxBuilders();
		for(JobBoxBuilder jb:jbs) {
			ja.add(new JsonParser().parse(new Gson().toJson(jb)).getAsJsonObject());
		}
		return ja;
	}
	

}