package com.zofund.jobdispatcher.service.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.zofund.jobdispatcher.service.engine.JobExecuteOperator;
import com.zofund.jobdispatcher.service.spring.SpringTool;

public class QuartzJobProxy implements Job {	
	
	public static String JOB_CODE = "jobCode";
	
	public static String EXCLUDED_DAY_STRATEGY_KEY = "EXCLUDED_DAY_STRATEGY_KEY";
	
	private static String NON_TRADING_DAY = "nonTradingDays";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	  String jobCode = (String) context.getMergedJobDataMap().get(JOB_CODE);
	  String filterCalendarName = (String) context.getMergedJobDataMap().get(EXCLUDED_DAY_STRATEGY_KEY);
	  if(NON_TRADING_DAY.equals(filterCalendarName)||null==filterCalendarName){
		  ExcludedDaysService excludedDaysService = SpringTool.getBean(ExcludedDaysService.class);
		  if(excludedDaysService.nowIsTradingDay()||null==filterCalendarName) {
			  JobExecuteOperator jobExecuteOperator = SpringTool.getBean(JobExecuteOperator.class);
			  jobExecuteOperator.triggerJob(jobCode);
		  }
	  }
	}

}
