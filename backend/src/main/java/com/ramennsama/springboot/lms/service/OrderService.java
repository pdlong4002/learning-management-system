package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.dto.request.CheckoutRequest;
import com.ramennsama.springboot.lms.dto.response.CheckoutResponse;
import com.ramennsama.springboot.lms.dto.response.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {
    CheckoutResponse checkout(CheckoutRequest request, HttpServletRequest httpRequest);
    List<OrderResponse> getMyOrders();
    OrderResponse getOrderDetail(Long orderId);
}
