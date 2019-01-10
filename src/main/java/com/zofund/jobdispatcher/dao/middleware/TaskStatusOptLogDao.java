package com.zofund.jobdispatcher.dao.middleware;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zofund.jobdispatcher.model.middleware.TaskProcessStatus;
import com.zofund.jobdispatcher.model.middleware.TaskStatusOptLog;

public interface TaskStatusOptLogDao  extends JpaRepository<TaskStatusOptLog,Long>{

	@Query(value = "select c_opttime from(select * from  t_task_opt_log where c_jobcode=?1 and c_taskcode=?2"
			+ " and c_eventtime=?3 and c_afterstatus='STARTED' order by c_opttime) where rownum = 1",nativeQuery = true)
	public String getStartedTime(String jobCode,String taskCode,String eventTime);
	
	@Query(value = "select task_opt_sequence.nextval from dual",nativeQuery = true)
	public String getOptSeq();

	public List<TaskStatusOptLog> findAllByJobCodeAndEventTimeAndTaskCodeOrderById(String jobCode, String eventTime,
			String taskCode);

	public List<TaskStatusOptLog> findByJobCodeAndEventTimeAndTaskCodeAndAfterStatusOrderById(String jobCode,
			String eventTime, String taskCode, TaskProcessStatus status);
	
}
