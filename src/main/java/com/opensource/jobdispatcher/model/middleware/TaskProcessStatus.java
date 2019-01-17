package com.opensource.jobdispatcher.model.middleware;

public enum TaskProcessStatus {
	
	    UNSTART("未开始", 0),TRIGGERED("正在触发", 1),
	    STARTED("正在执行", 2),NEED_RESTART("需重新执行", 3),PAUSED("已暂停", 4),FINISHED("已完毕", 5);
	
	    // 成员变量
	    private String name;
	    private int index;

	    // 构造方法
	    private TaskProcessStatus(String name, int index) {
	        this.name = name;
	        this.index = index;
	    }
	    
	    // 普通方法
	    public static TaskProcessStatus getTaskProcessStatus(String str) {
	    	switch(str){  
	    	        case "UNSTART":
	    	        	return TaskProcessStatus.UNSTART;
	    	        case "TRIGGERED":  
	    	        	return TaskProcessStatus.TRIGGERED;
	    	        case "STARTED":  
	    	        	return TaskProcessStatus.STARTED;
	    	        case "NEED_RESTART":  
	    	        	return TaskProcessStatus.NEED_RESTART;
	    	        case "PAUSED":
	    	        	return TaskProcessStatus.PAUSED;
	    	        case "FINISHED":
	    	        	return TaskProcessStatus.FINISHED;
	    	       }
			return null;  
	    }
	    
	    // 普通方法
	    public static TaskProcessStatus getTaskProcessStatusAccName(String str) {
	    	switch(str){  
	    	        case "未开始":
	    	        	return TaskProcessStatus.UNSTART;
	    	        case "正在触发":  
	    	        	return TaskProcessStatus.TRIGGERED;
	    	        case "正在执行":  
	    	        	return TaskProcessStatus.STARTED;
	    	        case "需重新执行":  
	    	        	return TaskProcessStatus.NEED_RESTART;
	    	        case "已暂停":
	    	        	return TaskProcessStatus.PAUSED;
	    	        case "已完毕":
	    	        	return TaskProcessStatus.FINISHED;
	    	       }
			return null;  
	    }

	    // 普通方法
	    public static TaskProcessStatus getTaskProcessStatus(int index) {
	        for (TaskProcessStatus status : TaskProcessStatus.values()) {
	        	if (status.getIndex() == index) {
	        		return status;
	        	}
	        }
	        return null;
	    }

	    // get set 方法
	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public int getIndex() {
	        return index;
	    }

	    public void setIndex(int index) {
	        this.index = index;
	    }
	}