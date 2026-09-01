package com.ramennsama.springboot.lms.dto.request;

import com.ramennsama.springboot.lms.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    
    @NotEmpty(message = "Course list cannot be empty")
    private List<Long> courseIds;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
