package com.opensource.jobdispatcher.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opensource.jobdispatcher.model.middleware.builder.JobBoxBuilder;

@Service
@EnableScheduling
public class MessageService{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
    @Value("${admin.mobilenos}")
	private String adminMobileNos;

	private static final Integer MAX_NO_HOUR = 3;
	
	private HashMap<String,Integer> sameSmsHaveSent = new HashMap<String,Integer>();
	
	@Autowired
	private JobBoxBuilderRegisterCenter jobBoxBuilderRegisterCenter;
	
	@Scheduled(cron = "0 0 * * * ?")//每隔一小时清空sameSmsHaveSent
	public void clearDupliSms() throws Exception{
		sameSmsHaveSent = new HashMap<String,Integer>();
	}

	public void smsAlert(String jobCode,String content){
		try {
			JobBoxBuilder jbb = jobBoxBuilderRegisterCenter.getJobBoxBuilderByJobCode(jobCode);
			if(jbb != null) {
				HashSet<String> mobileSet = new HashSet<String>(Arrays.asList(adminMobileNos.split(",")));
				String mp = jbb.getMonitorParas();
				if(mp!=null) {
					JsonObject jo = new JsonParser().parse(mp).getAsJsonObject();
					if(null!=jo.get("mobileNos")) {
						JsonArray mobileNos = jo.get("mobileNos").getAsJsonArray();
						for(JsonElement mobileNo:mobileNos) {
							mobileSet.add(mobileNo.getAsString());
						}
					}
				
					log.info("<<<<<Start Send Sms :mobilenos is " + mobileSet.toString());
					if(null == sameSmsHaveSent.get(mobileSet.toString()+content)||sameSmsHaveSent.get(mobileSet.toString()+content)<MAX_NO_HOUR) {
						String resulut = sendSms(mobileSet.toString(),content);
						log.info("<<<<<messageFeignService.sendSms result is " + resulut);
						int i = 0;
						if(sameSmsHaveSent.get(mobileSet.toString()+content)!=null) {
							i = sameSmsHaveSent.get(mobileSet.toString()+content);
						}
						sameSmsHaveSent.put(mobileSet.toString()+content,i+1);
					}
				}
			}
		}catch(Exception e) {
			log.error("####告警中心异常 "+ e.getMessage());
		}
	}

	private String sendSms(String string, String content) {
		// TODO Auto-generated method stub
		return null;
	}
    

}
