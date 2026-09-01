package com.ramennsama.springboot.lms.dto.response;

import com.ramennsama.springboot.lms.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private UserResponse user;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> orderItems;
}
