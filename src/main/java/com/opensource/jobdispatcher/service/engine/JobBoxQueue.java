package com.opensource.jobdispatcher.service.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opensource.jobdispatcher.model.middleware.JobBox;
import com.opensource.jobdispatcher.model.middleware.Task;
import com.opensource.jobdispatcher.model.middleware.TaskProcessStatus;
import com.opensource.jobdispatcher.service.spring.SpringTool;

public class JobBoxQueue{
	
	private String jobCode;
	
	private ConcurrentLinkedQueue<JobBox> queue = new ConcurrentLinkedQueue<JobBox>();
	
	private AtomicBoolean isblocked = new AtomicBoolean(false);

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
    private static Integer retryTimes = 5;
	
	private static Long SLEEP_TIME = 5000L;
	
	JobBoxQueue(@NotNull String jobCode) {
		this.jobCode = jobCode;
		//指向Job队列，等待任务的处理
		new Thread(() -> {
			log.info("我是线程"+Thread.currentThread().getName()+",我是一个永远在执行的守护线程");
			while(true) { // 永远在循环
				try{
					tryProcessQueueHead();
				}catch(Exception e) {
					log.error("#########单个任务处理的异常不应影响其他Job的线程-----");
					log.error(e.getMessage(),e);
				}
			}
		}).start();
	}

	private void tryProcessQueueHead(){
		JobBox jobBox = this.queue.peek(); //获取但不移除
		if(null!=jobBox&&this.blockQueue()) {//队列不为空,且该队列未阻塞
			log.info("####我是线程"+Thread.currentThread().getName()+"队列中有任务可以取出处理了");
			log.info("我是线程"+Thread.currentThread().getName()+",我用于处理 JobBox,jobCode is "+ jobBox.getJobCode()+"batchDate is "+ jobBox.getEventTime());
			process(jobBox);
			log.info("我是线程"+Thread.currentThread().getName()+",我已经处理完毕,即将释放");
		    this.queue.poll(); //获取并移除
		    this.releaseQueue();
		}
	}
	
	private void process(JobBox jobBox){
		Map<String, Integer> retryMap = new HashMap<String,Integer>();
		if(jobBox.getTasksList()!=null) {
			for(Task task:jobBox.getTasksList()) {
				retryMap.put(task.getTaskCode(), retryTimes);
			}
		}
		
		while(!jobBox.allTaskIsFinished()) {
			List<Task> tasksCanExecute = jobBox.getTasksCanExecute();
			if(null!=tasksCanExecute) {
				int cnt = tasksCanExecute.size();
				for(Task task:tasksCanExecute) {
					String taskCode = task.getTaskCode();
					if(retryMap.get(taskCode)>0) {
						if(!triggerTask(task)) {
							log.error("Triggertask fail retryTimes is " + retryMap.get(taskCode));
							sleep(cnt);
							retryMap.put(taskCode, retryMap.get(taskCode)-1);
						}
				    }else {
						JobExecuteOperator jobExecuteOperator = SpringTool.getBean(JobExecuteOperator.class);
				    	jobExecuteOperator.updateTaskStatus(jobCode,task.getTaskCode(),jobBox.getEventTime(),TaskProcessStatus.PAUSED,"retryFail");
					    String content = "jobCode:"+jobBox.getJobCode()+",taskCode:"+taskCode+",eventTime:"+jobBox.getEventTime()
					    					+"已重试"+retryTimes+"次,未能启动成功,task 地址 "+task.getAdapterPara();
					    log.info(content);
						retryMap.put(taskCode, retryTimes);
				    }
				}
			}
		}
		log.info("JobBox中可执行的task数量:"+jobBox.getTasksCanExecute().size()+"已经处理完毕");
	}

	private Boolean triggerTask(Task t){
		Boolean triggerIsSuc = false;
		try {
			log.info("###开始触发任务,springId is "+t.getAdapterSpringId()+t.getAdapterMethodName());
			JobExecuteEngine jobExecuteEngine = SpringTool.getBean(JobExecuteEngine.class);
			List<String> eventTimeList = jobExecuteEngine.getWaitingTasksEventTimeList(t.getJobCode());
			Object[] params = new Object[]{t.getJobCode(),t.getTaskCode(),eventTimeList,t.getAdapterPara()};
			triggerIsSuc = (Boolean)SpringTool.invokMethod(t.getAdapterSpringId(),t.getAdapterMethodName(),params);
			log.info("###触发任务结束############## triggerIsSuc is " + triggerIsSuc);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return triggerIsSuc;
	}

	public boolean blockQueue() {
		return this.isblocked.compareAndSet(false, true);
	}
	
	public boolean releaseQueue() {
		return this.isblocked.compareAndSet(true, false);
	}
	
	public JobBox getJobBox(String eventTime) {
		for(JobBox jb:queue) {
			String jbEventTime = jb.getEventTime();
			if(eventTime.equals(jbEventTime)) {
				return jb;
			}
		}
		return null;
	}

	public void put(JobBox jb) {
		queue.add(jb);
	}

	public Integer size() {
		return queue.size();
	}
	
	public List<JobBox> getQueueArray(){
		List<JobBox> jobBoxList = new ArrayList<JobBox>();
		for(JobBox jb:queue) {
			jobBoxList.add(jb);
		}
		return jobBoxList;
	}

	public String getJobCode() {
		return jobCode;
	}
	
	public JobBox getHead() {
		return queue.peek();
	}
	
	private void sleep(int cnt) {
		try {
			log.info("Thread will Sleep " + SLEEP_TIME/cnt);
			Thread.sleep(SLEEP_TIME/cnt);
		} catch (InterruptedException e1) {
			log.error("Thread Sleep Interrupted!",e1);
		}
	}


}
