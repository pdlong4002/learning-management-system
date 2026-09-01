package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.enums.PaymentMethod;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {

    String createPaymentUrl(Order order, PaymentMethod paymentMethod, HttpServletRequest request);

    boolean verifyCallback(PaymentMethod paymentMethod, Map<String, String> params);

    void completeOrder(Long orderId, String transactionId);
}
