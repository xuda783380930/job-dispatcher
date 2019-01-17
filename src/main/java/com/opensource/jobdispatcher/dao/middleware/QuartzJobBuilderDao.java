package com.opensource.jobdispatcher.dao.middleware;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.opensource.jobdispatcher.model.middleware.quartz.QuartzJob;

public interface QuartzJobBuilderDao extends JpaRepository<QuartzJob,String> {

	public List<QuartzJob> findAllByJobCode(String queryJobCode);

}
