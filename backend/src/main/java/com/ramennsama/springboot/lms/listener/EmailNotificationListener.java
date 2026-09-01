package com.ramennsama.springboot.lms.listener;

import com.ramennsama.springboot.lms.dto.event.OtpEvent;
import com.ramennsama.springboot.lms.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ramennsama.springboot.lms.dto.event.PaymentSuccessEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.repository.OrderRepository;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final EmailService emailService;
    private final OrderRepository orderRepository;

    @Async
    @EventListener
    public void handleOtpEvent(OtpEvent event) {
        log.info("[EVENT-DRIVEN] Received OtpEvent for {} (Type: {})", event.getEmail(), event.getEventType());
        log.info("[DEV-ONLY] OTP CODE for {} is: {}", event.getEmail(), event.getOtpCode()); // Only for Dev testing

        String subject = "";
        String title = "";
        String message = "";

        switch (event.getEventType()) {
            case REGISTER_OTP:
                subject = "LMS - Vui lòng xác thực tài khoản";
                title = "Xác thực tài khoản";
                message = "Mã OTP xác thực đăng ký tài khoản của bạn là:";
                break;
            case RESET_PASSWORD:
                subject = "LMS - Yêu cầu khôi phục mật khẩu";
                title = "Khôi phục mật khẩu";
                message = "Mã OTP để khôi phục mật khẩu của bạn là:";
                break;
            default:
                log.warn("Unknown OTP Event Type: {}", event.getEventType());
                return;
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("title", title);
        variables.put("message", message);
        variables.put("otpCode", event.getOtpCode());
        variables.put("duration", event.getDurationInMinutes());

        try {
            emailService.sendHtmlEmail(event.getEmail(), subject, "otp-email", variables);
            log.info("Successfully sent {} HTML email to {}", event.getEventType(), event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send {} HTML email to {}: {}", event.getEventType(), event.getEmail(), e.getMessage());
        }
    }

    @Async
    @EventListener
    @Transactional
    public void handlePaymentSuccessEvent(PaymentSuccessEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElseThrow();
        String email = order.getUser().getEmail();
        String subject = "LMS - Thanh toán đơn hàng thành công";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", order.getId());
        // Định dạng số tiền (cơ bản)
        variables.put("totalPrice", String.format("%,.0f", order.getTotalPrice()));

        log.info("[EVENT-DRIVEN] Bắt đầu gửi HTML email hóa đơn cho {} chạy dưới thread {}", email, Thread.currentThread().getName());

        try {
            emailService.sendHtmlEmail(email, subject, "payment-success-email", variables);
            log.info("Successfully sent PAYMENT SUCCESS HTML email to {}", email);
        } catch (Exception e) {
            log.error("Failed to send PAYMENT SUCCESS HTML email to {}: {}", email, e.getMessage());
        }
    }
}
