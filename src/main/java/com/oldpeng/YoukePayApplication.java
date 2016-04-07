package com.oldpeng;

import com.oldpeng.core.weixin.WeixinClientEngine;
import com.oldpeng.core.weixin.WeixinPayEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * Created by dapeng on 16/4/6.
 */
@SpringBootApplication
public class YoukePayApplication extends SpringBootServletInitializer {

	@Value("${pay.weixin.key}")
	private String key;

	@Value("${pay.weixin.appid}")
	private String appid;

	@Value("${pay.weixin.app_secret}")
	private String appSecret;

	@Value("${pay.weixin.mchid}")
	private String appMchid;

	@Value("${pay.weixin.notify_uri}")
	private String notifyUrl;

	@Value("${pay.weixin.authorize_redirect_uri}")
	private String authorizeRedirectUri;

	@Value("${pay.weixin.cert_password}")
	private String certPassword;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(YoukePayApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(YoukePayApplication.class);
		app.setAddCommandLineProperties(false);
		app.run(args);
	}

	@Bean
	public WeixinPayEngine weixinPayEngine(){
		WeixinPayEngine weixinPayEngine = new WeixinPayEngine();
		weixinPayEngine.setKey(key);
		weixinPayEngine.setAppid(appid);
		weixinPayEngine.setAppSecret(appSecret);
		weixinPayEngine.setAppMchid(appMchid);
		weixinPayEngine.setNotifyUrl(notifyUrl);
		weixinPayEngine.setAuthorizeRedirectUri(authorizeRedirectUri);
		weixinPayEngine.setCertPassword(certPassword);

		return weixinPayEngine;
	}

	@Bean
	public WeixinClientEngine weixinClientEngine(){
		WeixinClientEngine weixinClientEngine = new WeixinClientEngine();
		weixinClientEngine.setAppid(appid);
		weixinClientEngine.setAppSecret(appSecret);
		weixinClientEngine.setAuthorizeRedirectUri(authorizeRedirectUri);
		return weixinClientEngine;
	}
}
