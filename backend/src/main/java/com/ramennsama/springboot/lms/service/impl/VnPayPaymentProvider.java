package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.config.VnPayConfig;
import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.enums.PaymentMethod;
import com.ramennsama.springboot.lms.service.PaymentProvider;
import com.ramennsama.springboot.lms.utils.HmacUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class VnPayPaymentProvider implements PaymentProvider {

    private final VnPayConfig vnPayConfig;

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.VNPAY;
    }

    @Override
    public String createPaymentUrl(Order order, HttpServletRequest request) {
        Map<String, String> vnpParams = new HashMap<>();

        vnpParams.put("vnp_Version", vnPayConfig.getVersion());
        vnpParams.put("vnp_Command", vnPayConfig.getCommand());
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnpParams.put("vnp_TxnRef", String.valueOf(order.getId()));

        long vnpAmount = order.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue();
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_CurrCode", "VND");

        vnpParams.put("vnp_OrderInfo", "Thanh toan khoa hoc LMS - Ma " + order.getId());
        vnpParams.put("vnp_OrderType", vnPayConfig.getOrderType());
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnpParams.put("vnp_IpAddr", HmacUtil.getClientIp(request));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        vnpParams.put("vnp_CreateDate", formatter.format(calendar.getTime()));

        calendar.add(Calendar.MINUTE, vnPayConfig.getExpireMinutes());
        vnpParams.put("vnp_ExpireDate", formatter.format(calendar.getTime()));

        // Build query string và tạo chữ ký
        String query = HmacUtil.buildSortedQuery(vnpParams);
        String secureHash = HmacUtil.hmacSHA512(vnPayConfig.getHashSecret(), query);
        String fullUrl = vnPayConfig.getPayUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;

        log.info("=== VNPay Payment URL Created ===");
        log.info("OrderId: {}, Amount: {}, URL: {}", order.getId(), order.getTotalPrice(), fullUrl);

        return fullUrl;
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        log.info("=== VNPay Callback Verification ===");

        Map<String, String> verifyParams = new HashMap<>(params);
        String vnpSecureHash = verifyParams.remove("vnp_SecureHash");
        verifyParams.remove("vnp_SecureHashType");

        String signData = HmacUtil.buildSortedQuery(verifyParams);
        String checkHash = HmacUtil.hmacSHA512(vnPayConfig.getHashSecret(), signData);

        boolean isValid = checkHash.equals(vnpSecureHash);
        log.info("Calculated Hash: {}", checkHash);
        log.info("Received Hash:   {}", vnpSecureHash);
        log.info("Verification Result: {}", isValid);

        return isValid;
    }
}
