package com.oldpeng.web.controller;

import com.oldpeng.core.utils.XmlUtils;
import com.oldpeng.core.weixin.PaymentJsBean;
import com.oldpeng.core.weixin.PaymentReturnBean;
import com.oldpeng.service.WeixinPayService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dapeng on 16/4/6.
 */
@Controller
//@RequestMapping("/texun")
@RequestMapping("/neibu")
public class IndexController {

	private static Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	private WeixinPayService weixinPayService;

	@RequestMapping("authorize/base")
	public String auth(@RequestParam("to") String redirectUrl){
		return "redirect:" + weixinPayService.buildAuthorizeUrl(redirectUrl);
	}

	@RequestMapping("authorize/enter")
	public String enter(@RequestParam String code, @RequestParam String state){
		String openId = weixinPayService.getOpenId(code);
		return "redirect:" + state + "?openId=" + openId;
	}

	//TODO 参数去掉require=false
	@RequestMapping("pay/{cent}")
	public String payment(@PathVariable Long cent,
	                      @RequestParam(value = "openId") String openId,
	                      @RequestHeader(value = "X-Real-IP") String remoteIp,
	                      Model model){
		try {
			PaymentJsBean paymentJsBean = weixinPayService.buildPaymentJsBean(openId, cent, remoteIp);
			model.addAttribute("paymentJsBean", paymentJsBean);
//			PaymentJsBean paymentJsBean = new PaymentJsBean();
//			paymentJsBean.setAppId("helloworld");
//			model.addAttribute("paymentJsBean", paymentJsBean);
			return "pay";
		} catch(Throwable t){
			logger.info(t.getMessage(), t);
			return "404";
		}
	}

	@RequestMapping("payment/callback")
	@ResponseBody
	public String callback(HttpServletRequest request){
		try {
			String paymentNotifyMsg = IOUtils.toString(request.getInputStream(), "UTF-8");
			logger.info("======-------=======--------: weixin payment callback: " + paymentNotifyMsg);
			weixinPayService.callback(paymentNotifyMsg);
		} catch(Throwable t){
			logger.info(t.getMessage(), t);
		}

		PaymentReturnBean paymentReturnBean = new PaymentReturnBean();
		paymentReturnBean.setReturnCode(PaymentReturnBean.Code.SUCCESS.toString());
		paymentReturnBean.setReturnMsg("OK");
		return XmlUtils.toXml(paymentReturnBean);
	}
}
