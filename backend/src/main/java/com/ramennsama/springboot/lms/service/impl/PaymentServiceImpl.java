package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.enums.OrderStatus;
import com.ramennsama.springboot.lms.enums.PaymentMethod;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;
import com.ramennsama.springboot.lms.dto.event.PaymentSuccessEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.ramennsama.springboot.lms.repository.OrderRepository;
import com.ramennsama.springboot.lms.service.PaymentProvider;
import com.ramennsama.springboot.lms.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final Map<PaymentMethod, PaymentProvider> providerMap;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentServiceImpl(
            List<PaymentProvider> providers,
            OrderRepository orderRepository,
            ApplicationEventPublisher eventPublisher) {
        this.providerMap = providers.stream()
                .collect(Collectors.toMap(PaymentProvider::getPaymentMethod, p -> p));
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;

        log.info("PaymentService initialized with {} provider(s): {}",
                providerMap.size(), providerMap.keySet());
    }

    @Override
    public String createPaymentUrl(Order order, PaymentMethod paymentMethod, HttpServletRequest request) {
        PaymentProvider provider = getProvider(paymentMethod);
        return provider.createPaymentUrl(order, request);
    }

    @Override
    public boolean verifyCallback(PaymentMethod paymentMethod, Map<String, String> params) {
        PaymentProvider provider = getProvider(paymentMethod);
        return provider.verifyCallback(params);
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId, String transactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.SUCCESS) {
            log.warn("Order {} already paid. Skipping.", orderId);
            return;
        }

        if (order.getStatus() == OrderStatus.EXPIRED) {
            throw new AppException(ErrorCode.ORDER_EXPIRED);
        }

        order.setStatus(OrderStatus.SUCCESS);
        order.setTransactionId(transactionId);
        orderRepository.save(order);

        log.info("Order {} marked as SUCCESS. TransactionId: {}", orderId, transactionId);

        // Bắn sự kiện thanh toán thành công để các module khác (Enrollment, Email) xử lý ngầm
        eventPublisher.publishEvent(new PaymentSuccessEvent(this, order.getId()));
        
        log.info("Order {} - Fired PaymentSuccessEvent for asynchronous processing", orderId);
    }

    private PaymentProvider getProvider(PaymentMethod paymentMethod) {
        PaymentProvider provider = providerMap.get(paymentMethod);
        if (provider == null) {
            throw new AppException(ErrorCode.PAYMENT_PROVIDER_NOT_SUPPORTED);
        }
        return provider;
    }
}
