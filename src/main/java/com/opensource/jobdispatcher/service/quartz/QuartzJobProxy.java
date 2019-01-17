package com.opensource.jobdispatcher.service.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.opensource.jobdispatcher.service.engine.JobExecuteOperator;
import com.opensource.jobdispatcher.service.spring.SpringTool;

public class QuartzJobProxy implements Job {	
	
	public static String JOB_CODE = "jobCode";
	
	public static String EXCLUDED_DAY_STRATEGY_KEY = "EXCLUDED_DAY_STRATEGY_KEY";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	  String jobCode = (String) context.getMergedJobDataMap().get(JOB_CODE);
	  String filterCalendarName = (String) context.getMergedJobDataMap().get(EXCLUDED_DAY_STRATEGY_KEY);
	  ExcludedDaysService excludedDaysService = SpringTool.getBean(ExcludedDaysService.class);
	  if(excludedDaysService.nowIsNotExcluded(filterCalendarName)) {
		  JobExecuteOperator jobExecuteOperator = SpringTool.getBean(JobExecuteOperator.class);
		  jobExecuteOperator.triggerJob(jobCode);
	  }
	}

}
