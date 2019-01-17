package com.opensource.jobdispatcher.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig{

	@Bean
	public RestTemplate restTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
         SSLContext sslContext = SSLContexts.custom()
                 .loadTrustMaterial(null, (arg0, arg1)->true)
                 .build();

         SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, null,null,(urlHostName,session)->true);

         CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

         HttpComponentsClientHttpRequestFactory requestFactory =new HttpComponentsClientHttpRequestFactory();
         requestFactory.setHttpClient(httpClient);
         requestFactory.setConnectTimeout(5000);// 设置超时 
         requestFactory.setConnectionRequestTimeout(5000);
         requestFactory.setReadTimeout(20000);
         
         RestTemplate restTemplate = new RestTemplate(requestFactory);
         
         return restTemplate;
	}

}
