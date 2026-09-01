package com.ramennsama.springboot.lms.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String code;
    private Double discountPercentage;
    private Double maxDiscountAmount;
    private Boolean isActive;
    private LocalDate expiryDate;
}
