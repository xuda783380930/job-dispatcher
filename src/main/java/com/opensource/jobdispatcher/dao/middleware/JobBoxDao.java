package com.opensource.jobdispatcher.dao.middleware;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.opensource.jobdispatcher.model.middleware.JobBox;

public interface JobBoxDao extends JpaRepository<JobBox,Long>{

	public List<JobBox> findByJobCodeAndEventTimeBetweenOrderByEventTime(String jobCode, String startDate, String endDate,Pageable pageable);

	public JobBox findOneByJobCodeAndEventTime(String jobCode, @NotNull String eventTime);

	public List<JobBox> findByEventTimeBetweenOrderByEventTime(String startDate, String endDate,Pageable pageable);

	@Query(value = "select * from t_jobbox where c_jobbox_id in "
			+ "(select distinct(c_jobbox_id) from t_task where c_taskprocessstatus !='FINISHED') "
			+ "order by c_eventtime",nativeQuery = true)
	public List<JobBox> getAllUnfinishedJobOrderByEventTimeAsc();
	
}
