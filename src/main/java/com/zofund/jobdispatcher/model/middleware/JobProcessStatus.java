package com.zofund.jobdispatcher.model.middleware;


//该状态是根据Job中的tasks的运行状态即时计算而来，并不会持久化
public enum JobProcessStatus {
	
	    UNSTART("未开始", 0),STARTED("已开始", 1),FINISHED("已完毕", 2);
	
	    // 成员变量
	    private String name;
	    private int index;

	    // 构造方法
	    private JobProcessStatus(String name, int index) {
	        this.name = name;
	        this.index = index;
	    }
	    
	    // 普通方法
	    public static JobProcessStatus getJobProcessStatus(String str) {
	    	switch(str){  
	    	        case "UNSTART":
	    	        	return JobProcessStatus.UNSTART;
	    	        case "STARTED":  
	    	        	return JobProcessStatus.STARTED;
	    	        case "FINISHED":
	    	        	return JobProcessStatus.FINISHED;
	    	       }
			return null;  
	    }

	    // 普通方法
	    public static JobProcessStatus getJobProcessStatus(int index) {
	        for (JobProcessStatus status : JobProcessStatus.values()) {
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