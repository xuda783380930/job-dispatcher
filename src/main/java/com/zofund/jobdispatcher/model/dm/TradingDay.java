package com.zofund.jobdispatcher.model.dm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "XEDM_DM.DIM_TIME")
public class TradingDay implements Serializable {

	private static final long serialVersionUID = 4799400887859795225L;
	
	@Id
	@Column(name = "SK_DATE")
	private Long skDate;
	
	@Column(name = "BK_DATE",length = 200)
	private String bkDate;

	public Long getSkDate() {
		return skDate;
	}

	public void setSkDate(Long skDate) {
		this.skDate = skDate;
	}

	public String getBkDate() {
		return bkDate;
	}

	public void setBkDate(String bkDate) {
		this.bkDate = bkDate;
	}

}
