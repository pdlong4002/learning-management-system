package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.config.MoMoConfig;
import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.enums.PaymentMethod;
import com.ramennsama.springboot.lms.service.PaymentProvider;
import com.ramennsama.springboot.lms.utils.HmacUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MoMoPaymentProvider implements PaymentProvider {

    private final MoMoConfig moMoConfig;
    private final ObjectMapper objectMapper;

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.MOMO;
    }

    @Override
    public String createPaymentUrl(Order order, HttpServletRequest request) {
        try {
            String orderId = String.valueOf(order.getId());
            String requestId = UUID.randomUUID().toString();
            String amount = String.valueOf(order.getTotalPrice().longValue());
            String orderInfo = "Thanh toan khoa hoc LMS - Ma " + orderId;
            String requestType = "captureWallet";
            String extraData = "";

            // Build raw signature theo tài liệu MoMo API v2
            String rawSignature = "accessKey=" + moMoConfig.getAccessKey()
                    + "&amount=" + amount
                    + "&extraData=" + extraData
                    + "&ipnUrl=" + moMoConfig.getIpnUrl()
                    + "&orderId=" + orderId
                    + "&orderInfo=" + orderInfo
                    + "&partnerCode=" + moMoConfig.getPartnerCode()
                    + "&redirectUrl=" + moMoConfig.getReturnUrl()
                    + "&requestId=" + requestId
                    + "&requestType=" + requestType;

            String signature = HmacUtil.hmacSHA256(moMoConfig.getSecretKey(), rawSignature);

            // Build JSON request body
            Map<String, Object> requestBody = Map.ofEntries(
                    Map.entry("partnerCode", moMoConfig.getPartnerCode()),
                    Map.entry("partnerName", "LMS Platform"),
                    Map.entry("storeId", "LMS_Store"),
                    Map.entry("requestId", requestId),
                    Map.entry("amount", Long.parseLong(amount)),
                    Map.entry("orderId", orderId),
                    Map.entry("orderInfo", orderInfo),
                    Map.entry("redirectUrl", moMoConfig.getReturnUrl()),
                    Map.entry("ipnUrl", moMoConfig.getIpnUrl()),
                    Map.entry("lang", "vi"),
                    Map.entry("requestType", requestType),
                    Map.entry("autoCapture", true),
                    Map.entry("extraData", extraData),
                    Map.entry("signature", signature)
            );

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // Gửi POST request tới MoMo API
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(moMoConfig.getEndpoint()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode jsonResponse = objectMapper.readTree(response.body());
            String payUrl = jsonResponse.has("payUrl") ? jsonResponse.get("payUrl").asText() : null;

            log.info("=== MoMo Payment URL Created ===");
            log.info("OrderId: {}, Amount: {}, PayUrl: {}", orderId, amount, payUrl);

            if (payUrl == null || payUrl.isEmpty()) {
                log.error("MoMo response: {}", response.body());
                throw new RuntimeException("MoMo did not return a payment URL. Response: " + response.body());
            }

            return payUrl;

        } catch (Exception e) {
            log.error("Error creating MoMo payment URL", e);
            throw new RuntimeException("Failed to create MoMo payment URL", e);
        }
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        log.info("=== MoMo Callback Verification ===");

        String receivedSignature = params.get("signature");

        // Rebuild signature theo thứ tự tài liệu MoMo
        String rawSignature = "accessKey=" + moMoConfig.getAccessKey()
                + "&amount=" + params.get("amount")
                + "&extraData=" + params.getOrDefault("extraData", "")
                + "&message=" + params.getOrDefault("message", "")
                + "&orderId=" + params.get("orderId")
                + "&orderInfo=" + params.getOrDefault("orderInfo", "")
                + "&orderType=" + params.getOrDefault("orderType", "")
                + "&partnerCode=" + params.get("partnerCode")
                + "&payType=" + params.getOrDefault("payType", "")
                + "&requestId=" + params.get("requestId")
                + "&responseTime=" + params.getOrDefault("responseTime", "")
                + "&resultCode=" + params.get("resultCode")
                + "&transId=" + params.getOrDefault("transId", "");

        String checkSignature = HmacUtil.hmacSHA256(moMoConfig.getSecretKey(), rawSignature);

        boolean isValid = checkSignature.equals(receivedSignature);
        log.info("Calculated Signature: {}", checkSignature);
        log.info("Received Signature:   {}", receivedSignature);
        log.info("Verification Result:  {}", isValid);

        return isValid;
    }
}
