package com.opensource.jobdispatcher.service.quartz;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.opensource.jobdispatcher.dao.dm.TradingDayDAO;
import com.opensource.jobdispatcher.model.dm.TradingDay;

@Service
public class ExcludedDaysService{
	
	@Autowired
	private TradingDayDAO tradingDayDAO;
	
	private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static String NON_TRADING_DAY = "nonTradingDays";
	
    private Boolean nowIsTradingDay(){
    	LocalDateTime localDateTime = LocalDateTime.now();
    	String now = DATE_FORMATTER.format(localDateTime);
		List<TradingDay> results = tradingDayDAO.getTradingDaysOfNow(now);
		if(results.size()>0){
			log.info("<<<<<<<<<<< "+ now  + " is trading Day");
			return true;
		}
		return false;
    }

	public boolean nowIsNotExcluded(String filterCalendarName) {
		if(NON_TRADING_DAY.equals(filterCalendarName)) {
			return nowIsTradingDay();
		}
		return true;
	}

}