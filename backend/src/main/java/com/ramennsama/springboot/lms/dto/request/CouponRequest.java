package com.ramennsama.springboot.lms.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequest {

    @NotNull(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Discount percentage is required")
    @DecimalMax(value = "100.0", inclusive = true)
    @DecimalMin(value = "0.0", inclusive = true)
    private Double discountPercentage;

    @NotNull(message = "Max discount amount is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private Double maxDiscountAmount;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    @Builder.Default
    private Boolean isActive = true;
}
