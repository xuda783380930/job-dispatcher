package com.opensource.jobdispatcher.service.quartz;

import java.util.List;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opensource.jobdispatcher.dao.middleware.QuartzJobBuilderDao;
import com.opensource.jobdispatcher.model.middleware.quartz.QuartzJob;

@Service
public class QuartzService{

    @Autowired
    private Scheduler scheduler;
    
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private QuartzJobBuilderDao quartzJobBuilderDao;
	
    @PostConstruct
	void init(){
    	List<QuartzJob> allQuartzJobs = getAllQuartzJobs();
    	if(null!=allQuartzJobs) {
    		for(QuartzJob quartzJob:allQuartzJobs) {
    			Boolean flag = registerOneQuartzJobIm(quartzJob);
    			if(flag) {
                    log.info("<<<<成功注册了一个定时任务 jobCode:" + quartzJob.getJobCode());
    			}
    		}
    	}
	}
    
    public Boolean registerQuartz(QuartzJob quartzJob) {
    	if(saveOrUpdateQuartzJob(quartzJob)){
    		return registerOneQuartzJobIm(quartzJob);
    	}
 	    return false;
    }
    
    public Boolean unregisterQuartzJob(String jobCode){
        if(!delQuartzJob(jobCode)) {
        	return false;
        }
        return removeQuartzJobIm(jobCode);
    }
	
    public List<QuartzJob> getQuartzJobsByCode(String queryJobCode) {
		return quartzJobBuilderDao.findAllByJobCode(queryJobCode);
	}
    
    public List<QuartzJob> getAllQuartzJobs() {
  		return quartzJobBuilderDao.findAll();
  	}

    @Modifying
    @Transactional
	public boolean saveOrUpdateQuartzJob(QuartzJob quartzJob) {
		String jobCode = quartzJob.getJobCode();
		delQuartzJob(jobCode);//删除
		quartzJobBuilderDao.save(quartzJob);//新增
		return true;
	}
	
    @Modifying
    @Transactional
	public boolean delQuartzJob(String jobCode) {
    	if(quartzJobBuilderDao.existsById(jobCode)) {
		   quartzJobBuilderDao.deleteById(jobCode); 
		}
		return true;
	}

	public Boolean removeQuartzJobIm(String jobCode){
		try {
			return scheduler.deleteJob(JobKey.jobKey(jobCode,  TriggerKey.DEFAULT_GROUP));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	private Boolean registerOneQuartzJobIm(QuartzJob quartzJob){
		try {
    		String jobCode = quartzJob.getJobCode();
    		
    		String filterCalendarName = quartzJob.getFilterCalendarName();
    		
            TriggerKey triggerKey = TriggerKey.triggerKey(jobCode, TriggerKey.DEFAULT_GROUP);

            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey); 
            
            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.
            		cronSchedule(quartzJob.getJobCronExpression()).inTimeZone(TimeZone.getTimeZone("GMT+8"));

            //不存在，创建一个
            if (null == trigger) {
                JobDetail jobDetail = JobBuilder.newJob(QuartzJobProxy.class) 
                    .withIdentity(jobCode, TriggerKey.DEFAULT_GROUP).build();
            	  //按新的cronExpression表达式构建一个新的trigger
                trigger = TriggerBuilder.newTrigger()
                			.withIdentity(jobCode, TriggerKey.DEFAULT_GROUP)
                			.withSchedule(scheduleBuilder)
           			     	.startNow()
           			     	.build();
                jobDetail.getJobDataMap().put(QuartzJobProxy.JOB_CODE, jobCode);
                jobDetail.getJobDataMap().put(QuartzJobProxy.EXCLUDED_DAY_STRATEGY_KEY, filterCalendarName);
                scheduler.scheduleJob(jobDetail, trigger);
            }else {
            	trigger = trigger.getTriggerBuilder()
            			         .withIdentity(triggerKey)
            			         .withSchedule(scheduleBuilder)
            			         .startNow()
            			         .build();
            	trigger.getJobDataMap().put(QuartzJobProxy.JOB_CODE, jobCode);
            	trigger.getJobDataMap().put(QuartzJobProxy.EXCLUDED_DAY_STRATEGY_KEY, filterCalendarName);
            	log.info("<<<<成功更新了一个定时任务");
            	scheduler.rescheduleJob(triggerKey, trigger);
            }
        return true;
    } catch (SchedulerException e) {
        log.error(e.getMessage(),e);
        return false;
    }
	}

}