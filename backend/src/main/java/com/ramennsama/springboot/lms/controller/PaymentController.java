package com.ramennsama.springboot.lms.controller;

import com.ramennsama.springboot.lms.enums.PaymentMethod;
import com.ramennsama.springboot.lms.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * VNPay IPN Callback (GET) — Called by VNPay Server, NOT by user.
     */
    @GetMapping("/callback/vnpay")
    public ResponseEntity<String> vnpayCallback(@RequestParam Map<String, String> params) {
        log.info("VNPay IPN callback received: {}", params);

        boolean isValid = paymentService.verifyCallback(PaymentMethod.VNPAY, params);

        if (!isValid) {
            log.warn("VNPay callback - Invalid signature!");
            return ResponseEntity.ok("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
        }

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        String transactionNo = params.get("vnp_TransactionNo");

        if ("00".equals(responseCode)) {
            try {
                Long orderId = Long.parseLong(txnRef);
                paymentService.completeOrder(orderId, transactionNo);
                log.info("VNPay payment SUCCESS for Order {}", orderId);
                return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
            } catch (Exception e) {
                log.error("Error processing VNPay callback for txnRef: {}", txnRef, e);
                return ResponseEntity.ok("{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}");
            }
        } else {
            log.warn("VNPay payment FAILED for txnRef: {}, responseCode: {}", txnRef, responseCode);
            return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
        }
    }

    /**
     * MoMo IPN Callback (POST) — Called by MoMo Server, NOT by user.
     */
    @PostMapping("/callback/momo")
    public ResponseEntity<Map<String, Object>> momoCallback(@RequestBody Map<String, String> params) {
        log.info("MoMo IPN callback received: {}", params);

        boolean isValid = paymentService.verifyCallback(PaymentMethod.MOMO, params);

        if (!isValid) {
            log.warn("MoMo callback - Invalid signature!");
            return ResponseEntity.ok(Map.of("status", 1, "message", "Invalid signature"));
        }

        String resultCode = params.get("resultCode");
        String orderId = params.get("orderId");
        String transId = params.get("transId");

        if ("0".equals(resultCode)) {
            try {
                Long orderIdLong = Long.parseLong(orderId);
                paymentService.completeOrder(orderIdLong, transId);
                log.info("MoMo payment SUCCESS for Order {}", orderId);
                return ResponseEntity.ok(Map.of("status", 0, "message", "Success"));
            } catch (Exception e) {
                log.error("Error processing MoMo callback for orderId: {}", orderId, e);
                return ResponseEntity.ok(Map.of("status", 1, "message", "Error"));
            }
        } else {
            log.warn("MoMo payment FAILED for orderId: {}, resultCode: {}", orderId, resultCode);
            return ResponseEntity.ok(Map.of("status", 0, "message", "Acknowledged"));
        }
    }
}
