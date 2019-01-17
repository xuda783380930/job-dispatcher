package com.opensource.jobdispatcher.config;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef="dmEntityManagerFactory",transactionManagerRef="dmTransactionManager",
basePackages= { "com.zofund.jobdispatcher.dao.dm" })
public class DmRepositoryConfig {
	
    @Autowired
    @Qualifier("dmDS")
    private DataSource dmDataSource;
 
    @Autowired 
    private JpaProperties jpaProperties;

    @Bean(name = "dmEntityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return dmEntityManagerFactory(builder).getObject().createEntityManager();
    }

    @Bean(name = "dmEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dmEntityManagerFactory(EntityManagerFactoryBuilder builder) {
	     return builder.dataSource(dmDataSource)
	                //设置数据源属性
	                .properties(getVendorProperties())//设置实体类所在位置.扫描所有带有 @Entity 注解的类
	                .packages("com.zofund.jobdispatcher.model.dm")//设置实体类所在位置
	                // Spring会将EntityManagerFactory注入到Repository之中.有了 EntityManagerFactory之后,
	                // Repository就能用它来创建 EntityManager 了,然后Entity就可以针对数据库执行操作
	                .persistenceUnit("dmPersistenceUnit")
	                .build();
	}
    
	private Map<String, Object> getVendorProperties() {
        return jpaProperties.getHibernateProperties(new HibernateSettings());
	}
	
    @Bean(name = "dmTransactionManager")
    PlatformTransactionManager dmTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(dmEntityManagerFactory(builder).getObject());
    }

}