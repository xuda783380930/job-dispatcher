package com.zofund.jobdispatcher.model.middleware.vo;

public class LinkVO{

	private String upStreamJobCode;

	private String downStreamJobCode;

	public String getUpStreamJobCode() {
		return upStreamJobCode;
	}

	public void setUpStreamJobCode(String upStreamJobCode) {
		this.upStreamJobCode = upStreamJobCode;
	}

	public String getDownStreamJobCode() {
		return downStreamJobCode;
	}

	public void setDownStreamJobCode(String downStreamJobCode) {
		this.downStreamJobCode = downStreamJobCode;
	}
    
}
