package com.ramennsama.springboot.lms.mapper;

import com.ramennsama.springboot.lms.dto.request.CouponRequest;
import com.ramennsama.springboot.lms.dto.response.CouponResponse;
import com.ramennsama.springboot.lms.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    
    @Mapping(target = "id", ignore = true)
    Coupon toCoupon(CouponRequest request);

    @Mapping(target = "id", ignore = true)
    void updateCouponFromRequest(CouponRequest request, @MappingTarget Coupon coupon);

    CouponResponse toCouponResponse(Coupon coupon);
}
