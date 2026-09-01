package com.ramennsama.springboot.lms.dto.response;

import com.ramennsama.springboot.lms.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {
    private Long orderId;
    private String paymentUrl;
    private PaymentMethod paymentMethod;
}
