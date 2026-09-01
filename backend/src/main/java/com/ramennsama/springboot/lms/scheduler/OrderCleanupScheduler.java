package com.ramennsama.springboot.lms.scheduler;

import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.enums.OrderStatus;
import com.ramennsama.springboot.lms.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCleanupScheduler {

    private final OrderRepository orderRepository;

    // Chạy mỗi 5 phút một lần
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void cleanupExpiredOrders() {
        log.info("[SCHEDULER] Bắt đầu quét các đơn hàng PENDING đã hết hạn...");

        // Tìm các đơn hàng PENDING được tạo cách đây hơn 15 phút
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(15);
        List<Order> expiredOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, expiryTime);

        if (expiredOrders.isEmpty()) {
            log.info("[SCHEDULER] Không có đơn hàng nào hết hạn.");
            return;
        }

        // Chuyển trạng thái sang EXPIRED
        expiredOrders.forEach(order -> {
            order.setStatus(OrderStatus.EXPIRED);
            log.info("[SCHEDULER] Hủy đơn hàng hết hạn ID: {}", order.getId());
        });

        orderRepository.saveAll(expiredOrders);
        
        log.info("[SCHEDULER] Đã xử lý hủy thành công {} đơn hàng.", expiredOrders.size());
    }
}
