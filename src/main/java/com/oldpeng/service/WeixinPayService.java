package com.oldpeng.service;

import com.google.common.collect.Maps;
import com.oldpeng.core.utils.UuidUtils;
import com.oldpeng.core.utils.WeixinApiUtils;
import com.oldpeng.core.utils.XmlUtils;
import com.oldpeng.core.weixin.*;
import com.oldpeng.domain.WeixinPayment;
import com.oldpeng.repository.WeixinPaymentRepository;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by dapeng on 16/4/6.
 */
@Service
@Transactional
public class WeixinPayService {

	private static Logger logger = LoggerFactory.getLogger(WeixinPayService.class);

	@Autowired
	private WeixinPayEngine weixinPayEngine;

	@Autowired
	private WeixinClientEngine weixinClientEngine;

	@Autowired
	private WeixinPaymentRepository weixinPaymentRepository;

	private Map<Long, String> productMap;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");

	@PostConstruct
	public void init(){
		productMap = Maps.newHashMap();
		productMap.put(5000l, "【门票】微信营销新趋势实操大会");
		productMap.put(19900l, "【门票】微信营销新趋势实操大会");
		productMap.put(199900l, "【特训】全网营销实战训练营");
		productMap.put(299900l, "【特训】全网营销实战训练营");
		productMap.put(399900l, "【特训】全网营销实战训练营");
	}

	public String buildAuthorizeUrl(String redirectUrl) {
		return weixinClientEngine.buildAuthorizationUrl(WeixinUtils.SCOPE_BASE, redirectUrl);
	}

	public String getOpenId(String code) {
		UserAccessToken userAccessToken = weixinClientEngine.getWebpageUserAccessToken(code);
		return userAccessToken.getOpenid();
	}

	public PaymentJsBean buildPaymentJsBean(String openId, Long cent, String remoteIp) {
		PaymentOrderBean paymentOrderBean = new PaymentOrderBean();
		paymentOrderBean.setOutTradeNo(UuidUtils.generate());
		paymentOrderBean.setOpenid(openId);
		paymentOrderBean.setTotalFee(cent);
		paymentOrderBean.setBody(productMap.get(cent) == null ? "[其他]": productMap.get(cent));
		paymentOrderBean.setSpbillCreateIp(StringUtils.isBlank(remoteIp) ? "210.73.212.98" : remoteIp);
		paymentOrderBean.setSign(WeixinApiUtils.getSign(paymentOrderBean.retrieveStringProp(), weixinPayEngine.getKey()));
		PaymentOrderReturnBean paymentOrderReturnBean = weixinPayEngine.getUnifiedOrder(paymentOrderBean, null);

		String mySign = WeixinApiUtils.getSign(paymentOrderReturnBean.retrieveStringProp(), weixinPayEngine.getKey());
		if(mySign == null || !mySign.equals(paymentOrderReturnBean.getSign())){
			throw new RuntimeException("unifiedorder fail");
		}

		WeixinPayment weixinPayment = new WeixinPayment();
		weixinPayment.setNonceStr(paymentOrderBean.getNonceStr());
		weixinPayment.setOutTradeNo(paymentOrderBean.getOutTradeNo());
		weixinPayment.setOpenId(paymentOrderBean.getOpenid());
		weixinPayment.setAppid(weixinPayEngine.getAppid());
		weixinPayment.setBody(paymentOrderBean.getBody());
		weixinPaymentRepository.save(weixinPayment);

		PaymentJsBean paymentJsBean = new PaymentJsBean();
		paymentJsBean.setAppId(weixinPayment.getAppid());
		paymentJsBean.setNonceStr(weixinPayment.getNonceStr());
		paymentJsBean.setPackagePrepayId("prepay_id=" + paymentOrderReturnBean.getPrepayId());
		paymentJsBean.setPaySign(WeixinApiUtils.getSign(paymentJsBean.retrieveStringProp(), weixinPayEngine.getKey()));
		return paymentJsBean;
	}

	public void callback(String paymentCallbackMsg) {

		PaymentNotifyBean paymentNotifyBean = XmlUtils.toBean(paymentCallbackMsg, PaymentNotifyBean.class);

		WeixinPayment weixinPayment = weixinPaymentRepository.findOneByOutTradeNo(paymentNotifyBean.getOutTradeNo());
		if(weixinPayment == null){
			throw new RuntimeException(MessageFormat.format("payment {0} doesn't exist, ", paymentNotifyBean.getOutTradeNo()));
		}

		String key = weixinPayEngine.getKey();

		if(paymentNotifyBean != null && PaymentNotifyBean.Code.SUCCESS.toString().equals(paymentNotifyBean.getReturnCode())){
			String mySign = WeixinApiUtils.getSign(paymentNotifyBean.retrieveStringProp(), key);

			if(mySign == null || !mySign.equals(paymentNotifyBean.getSign())){
				throw new RuntimeException(MessageFormat.format("payment {0} sign error, ", paymentNotifyBean.getOutTradeNo()));
			}

			logger.info("========payment callback sign success:");


			if(weixinPayment != null && WeixinPayment.PayType.SUCCESS.getId() != weixinPayment.getPayType()){
				weixinPayment.setTotalFee(Long.valueOf(paymentNotifyBean.getTotalFee()).intValue());
				weixinPayment.setPayType(WeixinPayment.PayType.SUCCESS.getId());

				weixinPayment.setAppid(paymentNotifyBean.getAppid());
				weixinPayment.setMchId(paymentNotifyBean.getMchId());
				weixinPayment.setBankType(paymentNotifyBean.getBankType());
				weixinPayment.setFeeType(paymentNotifyBean.getFeeType());
				weixinPayment.setNonceStr(paymentNotifyBean.getNonceStr());
				weixinPayment.setTradeType(paymentNotifyBean.getTradeType());
				weixinPayment.setTransactionId(paymentNotifyBean.getTransactionId());
				weixinPayment.setSubscribed("Y".equalsIgnoreCase(paymentNotifyBean.getSubscribe()));
				weixinPayment.setNotifyTime(new Date());
				weixinPayment.setOpenId(paymentNotifyBean.getOpenid());

				String timeEnd = paymentNotifyBean.getTimeEnd();
				if(StringUtils.isNotBlank(timeEnd)){
					weixinPayment.setPayTime(dateTimeFormatter.parseDateTime(timeEnd).toDate());
				}

			}
		}
	}
}
