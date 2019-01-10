package com.zofund.jobdispatcher.dao.middleware;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zofund.jobdispatcher.model.middleware.Task;

public interface TaskDao extends JpaRepository<Task,Long>{

	List<Task> findAllByJobCodeAndTaskCodeAndEventTime(String jobCode, String taskCode, String eventTime);
	
}
