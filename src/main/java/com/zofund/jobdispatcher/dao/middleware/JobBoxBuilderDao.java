package com.zofund.jobdispatcher.dao.middleware;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zofund.jobdispatcher.model.middleware.builder.JobBoxBuilder;

public interface JobBoxBuilderDao extends JpaRepository<JobBoxBuilder,String>{

	List<JobBoxBuilder> findAllByJobCode(String jobCode);
	
}
