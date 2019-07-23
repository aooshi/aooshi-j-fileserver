package org.aooshi.j.fileserver;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.MultipartConfigElement;

import org.aooshi.j.fileserver.filters.AccessAuthorizationFilter;
import org.aooshi.j.fileserver.filters.BasicAuthorizationFilter;
import org.aooshi.j.fileserver.filters.TokenAuthorizationFilter;
import org.aooshi.j.fileserver.util.AppConfiguration;
import org.aooshi.j.fileserver.util.JsonUserConfiguration;
import org.aooshi.j.fileserver.util.MimeConfiguration;
import org.aooshi.j.fileserver.util.PublicBucketConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class FileServerApplication {

	public static void main(String[] args) 
	{
		SpringApplication.run(FileServerApplication.class, args);
		
		//LocalTokenCache.Test();
	}
		
	@Bean
	public FilterRegistrationBean<BasicAuthorizationFilter> controlFilterRegistrationBean()
	{
        FilterRegistrationBean<BasicAuthorizationFilter> registrationBean = new FilterRegistrationBean<BasicAuthorizationFilter>();
        BasicAuthorizationFilter basicAuthFilter = new BasicAuthorizationFilter();
        registrationBean.setFilter(basicAuthFilter);
        //
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/control/*");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
	}
	
	@Bean
	public FilterRegistrationBean<TokenAuthorizationFilter> fileFilterRegistrationBean()
	{
        FilterRegistrationBean<TokenAuthorizationFilter> registrationBean = new FilterRegistrationBean<TokenAuthorizationFilter>();
        TokenAuthorizationFilter tokenAuthorizationFilter = new TokenAuthorizationFilter();
        registrationBean.setFilter(tokenAuthorizationFilter);
        //
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/file/*");
        registrationBean.setUrlPatterns(urlPatterns); 
        return registrationBean;
	}

    @Bean
    public FilterRegistrationBean<AccessAuthorizationFilter> accessFilterRegistrationBean()
    {
        FilterRegistrationBean<AccessAuthorizationFilter> registrationBean = new FilterRegistrationBean<AccessAuthorizationFilter>();
        AccessAuthorizationFilter authorizationFilter = new AccessAuthorizationFilter();
        registrationBean.setFilter(authorizationFilter);
        //
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/a/*");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }
	
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(AppConfiguration.singleton.getLimitMaxFileSize());
        factory.setMaxRequestSize(AppConfiguration.singleton.getLimitMaxRequestSize());
        return factory.createMultipartConfig();
    }

    //配置更新（每分钟）
    @Scheduled(fixedRate = 1000 * 60 * 1)
    private void loadConfiguration()
    {
        PublicBucketConfiguration.instance.load();
        JsonUserConfiguration.singleton.load();
        MimeConfiguration.instance.load();
    }
}
