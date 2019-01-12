package com.zofund.jobdispatcher.model.middleware.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.zofund.jobdispatcher.model.middleware.Task;
import com.zofund.jobdispatcher.model.middleware.builder.TaskBuilder;

public class JobStructVO{

    private List<LinkVO> links;
    
    public <T> JobStructVO(List<T> taskTList){
    	links = new ArrayList<LinkVO>();
		if(null!=taskTList) {
			String upstreamTaskCodeStr = null;
			String taskCode = null;
			for(T tT:taskTList) {
				if(tT.getClass().equals(TaskBuilder.class)) {
					 TaskBuilder tb = (TaskBuilder)tT;
					 upstreamTaskCodeStr =tb.getUpstreamTaskCodes();
					 taskCode = tb.getTaskCode();
				}else if(tT.getClass().equals(Task.class)) {
					 Task t = (Task)tT;
					 upstreamTaskCodeStr =t.getUpstreamTaskCodes();
					 taskCode = t.getTaskCode();
				}
				if(null!=upstreamTaskCodeStr) {
					String[] upstreamTaskCodes = upstreamTaskCodeStr.split(",");
					for(String upstreamTaskCode:upstreamTaskCodes) {
						    LinkVO lk = new LinkVO();
							lk.setUpStreamJobCode(upstreamTaskCode);
							lk.setDownStreamJobCode(taskCode);
							links.add(lk);
					}
				}else {
					LinkVO lk = new LinkVO();
					lk.setUpStreamJobCode(null);
					lk.setDownStreamJobCode(taskCode);
					links.add(lk);
				}
			}
		}
    }

	public List<String> getDownstreamTasks(String taskCode) {
		List<String> downStreamTasks = new ArrayList<String>();
		for(LinkVO lk:links) {
			if(null==taskCode) {
				if(null==lk.getUpStreamJobCode()) {
					downStreamTasks.add(lk.getDownStreamJobCode());
				}
			}else {
				if(taskCode.equals(lk.getUpStreamJobCode())) {
					downStreamTasks.add(lk.getDownStreamJobCode());
				}
			}
		}
		downStreamTasks.sort((String t1,String t2) -> deep(t2).compareTo(deep(t1)));
		return downStreamTasks;
	}
	
	public List<String> getDownstreamTasksSortByDeep(String taskCode) {
		List<String> downStreamTasks = getDownstreamTasks(taskCode);
		downStreamTasks.sort((String t1,String t2) -> deep(t2).compareTo(deep(t1)));
		return downStreamTasks;
	}
	
    public HashSet<String> getDuplicatedLinkNodes(String nowTaskCode){
		List<String> downStreamTasks = this.getDownstreamTasks(nowTaskCode);
		if(downStreamTasks!=null) {
			for(int i=0;i<downStreamTasks.size();i++) {
				if(null!=getDuplicatedLinkNodes(downStreamTasks.get(i))) {
					return getDuplicatedLinkNodes(downStreamTasks.get(i));
				}
				for(int j=i+1;j<downStreamTasks.size();j++) {
					if(isIn(downStreamTasks.get(i),downStreamTasks.get(j))){
						HashSet<String> hs = new HashSet<String>();
						hs.add(nowTaskCode);
						hs.add(downStreamTasks.get(i));
						hs.add(downStreamTasks.get(j));
						return hs ;
					}
				}
			}
		}
		return null;
	}
    
	private boolean isIn(String nowTaskCode, String taskGroupCode) {
		if(nowTaskCode==taskGroupCode) {
			return true;
		}
		List<String> downStreamTasks = this.getDownstreamTasks(taskGroupCode);
		if(downStreamTasks!=null) {
			for(String tb:downStreamTasks) {
				if(isIn(nowTaskCode,tb)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Integer deep(String nowTaskCode) {
		List<String> downStreamTasks = this.getDownstreamTasks(nowTaskCode);
		Integer maxDeep = 0 ; 
		if(null!=downStreamTasks) {
			for(String t:downStreamTasks) {
				if((deep(t)+1)>maxDeep) {
					maxDeep = deep(t)+1;
				}
			}
		}
		return maxDeep;
	}

    
}
