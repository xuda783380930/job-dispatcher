package com.opensource.jobdispatcher.dao.middleware;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.opensource.jobdispatcher.model.middleware.builder.JobBoxBuilder;

public interface JobBoxBuilderDao extends JpaRepository<JobBoxBuilder,String>{

	List<JobBoxBuilder> findAllByJobCode(String jobCode);
	
}
