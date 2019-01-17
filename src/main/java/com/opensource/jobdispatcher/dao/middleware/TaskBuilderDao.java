package com.opensource.jobdispatcher.dao.middleware;

import org.springframework.data.jpa.repository.JpaRepository;
import com.opensource.jobdispatcher.model.middleware.builder.TaskBuilder;

public interface TaskBuilderDao  extends JpaRepository<TaskBuilder,Long>{
	
}
