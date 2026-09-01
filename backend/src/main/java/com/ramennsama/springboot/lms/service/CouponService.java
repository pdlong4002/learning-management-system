package com.ramennsama.springboot.lms.service;
import java.util.List;
import com.ramennsama.springboot.lms.dto.request.CouponRequest;
import com.ramennsama.springboot.lms.dto.response.CouponResponse;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest request);

    List<CouponResponse> getAllCoupons();

    CouponResponse getCouponById(Long id);

    CouponResponse updateCoupon(Long id, CouponRequest request);

    CouponResponse deleteCoupon(Long id);

    CouponResponse activateCoupon(Long id);

    CouponResponse deactivateCoupon(Long id);
}
