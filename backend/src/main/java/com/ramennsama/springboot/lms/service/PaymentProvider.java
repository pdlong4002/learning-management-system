package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.enums.PaymentMethod;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentProvider {

    /**
     * Which payment method this provider handles.
     */
    PaymentMethod getPaymentMethod();

    /**
     * Create a payment URL for the given order.
     * @param order the order to create payment for
     * @param request the HTTP request (to extract client IP)
     * @return the payment URL to redirect the user to
     */
    String createPaymentUrl(Order order, HttpServletRequest request);

    /**
     * Verify the callback parameters from the payment gateway.
     * @param params the callback parameters
     * @return true if the signature is valid
     */
    boolean verifyCallback(Map<String, String> params);
}
