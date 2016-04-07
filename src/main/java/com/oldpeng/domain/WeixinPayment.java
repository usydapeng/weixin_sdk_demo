package com.oldpeng.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by dapeng on 16/4/6.
 */
@Entity
@Table(name = "weixin_payment")
public class WeixinPayment implements Serializable {

	private static final long serialVersionUID = -8660847922905656701L;

	@Id
	@GeneratedValue
	private Long id;

	private String openId;

	private Date createTime;

	private String outTradeNo;

	private Integer totalFee;

	@Column(columnDefinition = "tinyint(4) default 0")
	private int payType = 0;

	private String appid;

	private String mchId;

	private String nonceStr;

	@Column(columnDefinition = "tinyint(4) default 0")
	private boolean subscribed = false;

	private String tradeType;

	private String bankType;

	private String feeType;

	private String transactionId;

	private Date notifyTime;

	private Date payTime;

	private String body;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public Integer getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Date getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(Date notifyTime) {
		this.notifyTime = notifyTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	@PrePersist
	public void createdAt(){
		this.createTime = new Date();
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public enum PayType {

		UNPAY(0),

		SUCCESS(1);

		private int id;

		PayType(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}
}
