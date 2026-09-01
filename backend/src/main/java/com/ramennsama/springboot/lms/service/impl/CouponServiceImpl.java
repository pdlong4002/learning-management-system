package com.ramennsama.springboot.lms.service.impl;

import org.springframework.stereotype.Service;
import com.ramennsama.springboot.lms.dto.request.CouponRequest;
import com.ramennsama.springboot.lms.dto.response.CouponResponse;
import com.ramennsama.springboot.lms.entity.Coupon;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;
import com.ramennsama.springboot.lms.mapper.CouponMapper;
import com.ramennsama.springboot.lms.repository.CouponRepository;
import com.ramennsama.springboot.lms.service.CouponService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService{

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Override
    public CouponResponse createCoupon(CouponRequest request) {
        Coupon coupon = couponMapper.toCoupon(request);
        Coupon savedCoupon = couponRepository.save(coupon);
        return couponMapper.toCouponResponse(savedCoupon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(couponMapper::toCouponResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));
        return couponMapper.toCouponResponse(coupon);
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));
        couponMapper.updateCouponFromRequest(request, coupon);
        Coupon savedCoupon = couponRepository.save(coupon);
        return couponMapper.toCouponResponse(savedCoupon);
    }

    @Override
    @Transactional
    public CouponResponse deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));
        couponRepository.delete(coupon);
        return couponMapper.toCouponResponse(coupon);
    }

    @Override
    public CouponResponse activateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));
        coupon.setIsActive(true);
        Coupon savedCoupon = couponRepository.save(coupon);
        return couponMapper.toCouponResponse(savedCoupon);
    }

    @Override
    public CouponResponse deactivateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));
        coupon.setIsActive(false);
        Coupon savedCoupon = couponRepository.save(coupon);
        return couponMapper.toCouponResponse(savedCoupon);
    }
    
    
}
