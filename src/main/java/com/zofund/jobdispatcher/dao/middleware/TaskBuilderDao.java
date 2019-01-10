package com.zofund.jobdispatcher.dao.middleware;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zofund.jobdispatcher.model.middleware.builder.TaskBuilder;

public interface TaskBuilderDao  extends JpaRepository<TaskBuilder,Long>{
	
}
