package com.opensource.jobdispatcher.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {
	
	@Bean(name = "mdDS") 
	@Qualifier("mdDS")
	@Primary
	@ConfigurationProperties(prefix="spring.datasource.middleware")
	public DataSource mdDataSource(){
	   return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "dmDS") 
	@Qualifier("dmDS")
	@ConfigurationProperties(prefix="spring.datasource.dm")
	public DataSource dmDataSource(){
	   return DataSourceBuilder.create().build();
	}

}