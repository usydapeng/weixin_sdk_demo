package com.oldpeng.web.interceptor;

import com.oldpeng.core.utils.ApiUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by dapeng on 16/4/6.
 */
public class ObtainOpenIdInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Object openId = request.getParameter("openId");

		if(openId == null){
			response.sendRedirect("/texun/authorize/base?to=" + ApiUtils.encodeParam(request.getServletPath()));
			return false;
		}

		return true;
	}
}
