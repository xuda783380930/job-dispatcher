package com.opensource.jobdispatcher.dao.dm;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.opensource.jobdispatcher.model.dm.TradingDay;

public interface TradingDayDAO  extends JpaRepository<TradingDay,String>{
	
	@Query(value = "select * from xedm_dm.dim_time where bk_date>= TO_CHAR(SYSDATE,'YYYY-MM-DD')"
			+ " and isworkday='1' and BK_DATE = ?1",nativeQuery = true)
	public List<TradingDay> getTradingDaysOfNow(String dateStr);

}
