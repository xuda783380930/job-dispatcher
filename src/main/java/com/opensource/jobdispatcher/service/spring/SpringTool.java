package com.opensource.jobdispatcher.service.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SpringTool implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    
	private static final Logger log = LoggerFactory.getLogger(SpringTool.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringTool.applicationContext == null) {
        	SpringTool.applicationContext = applicationContext;
        }    
        log.info("---------------------------------------------------------------------");
        log.info("========ApplicationContext配置成功,在普通类可以通过调用SpringUtils.getAppContext()获取applicationContext对象,"
        		+ "applicationContext="+SpringTool.applicationContext+"========");
        log.info("---------------------------------------------------------------------");
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
    	while(null==applicationContext) {
    		try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(),e);
			}
    	}
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }
    
    /*** 通过反射调用scheduleJob中定义的方法   * 
     * @param instant 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws Exception */  
    @SuppressWarnings({ "unchecked", "rawtypes" })  
    public static Object invokMethod(String springId,String methodName,Object[] params) throws Exception{    
    	Object object = null;    
    	Class clazz = null;
    	if (!StringUtils.isEmpty(springId)) {
    		object = SpringTool.getBean(springId); 
    	}
    	
    	if (object == null) {              
    		return null;    
    		}    
    	clazz = object.getClass();    
    	Method method = null;    
    	try {
    		Class[] classes = new Class[params.length];
    		for(int i=0;i<params.length;i++) {
    			classes[i] = params[i].getClass();
    		}
    		method = clazz.getDeclaredMethod(methodName, classes);    
    		} catch (NoSuchMethodException e) {          
    			log.error("任务名称 = [" + methodName + "]---------------未启动成功，方法名设置错误！！！"); 
    			} catch (SecurityException e) {        
    				e.printStackTrace();    
    		}
    	
    	if (method != null) {        
    			return method.invoke(object,params);         
    	}  
    	
		return null;
    }
 

}