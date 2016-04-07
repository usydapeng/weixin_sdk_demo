package com.oldpeng.config;

import com.oldpeng.web.interceptor.ObtainOpenIdInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by dapeng on 16/4/6.
 */

//TODO
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(obtainOpenIdInterceptor())
				.addPathPatterns("/texun/pay")
				.addPathPatterns("/texun/pay/*")
				.addPathPatterns("/texun/pay/**");
		super.addInterceptors(registry);
	}

	@Bean
	public ObtainOpenIdInterceptor obtainOpenIdInterceptor(){
		return new ObtainOpenIdInterceptor();
	}
}
