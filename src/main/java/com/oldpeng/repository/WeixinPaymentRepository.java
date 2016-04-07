package com.oldpeng.repository;

import com.oldpeng.domain.WeixinPayment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by dapeng on 16/4/6.
 */
public interface WeixinPaymentRepository extends JpaRepository<WeixinPayment, Long> {

	WeixinPayment findOneByOutTradeNo(String outTradeNo);

}
