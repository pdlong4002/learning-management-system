package com.ramennsama.springboot.lms.listener;

import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.dto.event.PaymentSuccessEvent;
import com.ramennsama.springboot.lms.repository.EnrollmentRepository;
import com.ramennsama.springboot.lms.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.ramennsama.springboot.lms.repository.OrderRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseEnrollmentListener {

    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;
    private final OrderRepository orderRepository;

    @Async
    @EventListener
    @Transactional
    public void handlePaymentSuccessEvent(PaymentSuccessEvent event) {
        Order order = orderRepository.findById(event.getOrderId()).orElseThrow();
        log.info("[EVENT-DRIVEN] Bắt đầu cấp quyền khóa học cho đơn hàng {} chạy dưới thread {}", order.getId(), Thread.currentThread().getName());

        order.getOrderItems().forEach(item -> {
            Long userId = order.getUser().getId();
            Long courseId = item.getCourse().getId();
            if (!enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
                enrollmentService.enrollUserInCourse(order.getUser(), item.getCourse());
                log.info("[EVENT-DRIVEN] Đã cấp quyền khóa học ID {} cho user ID {}", courseId, userId);
            }
        });
    }
}
