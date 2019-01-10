package com.zofund.jobdispatcher.dao.middleware;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zofund.jobdispatcher.model.middleware.quartz.QuartzJob;

public interface QuartzJobBuilderDao extends JpaRepository<QuartzJob,String> {

	public List<QuartzJob> findAllByJobCode(String queryJobCode);

}
