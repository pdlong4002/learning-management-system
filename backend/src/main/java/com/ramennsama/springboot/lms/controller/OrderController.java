package com.ramennsama.springboot.lms.controller;

import com.ramennsama.springboot.lms.dto.request.CheckoutRequest;
import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.CheckoutResponse;
import com.ramennsama.springboot.lms.dto.response.OrderResponse;
import com.ramennsama.springboot.lms.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(
            @Valid @RequestBody CheckoutRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CheckoutResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Order created. Redirect to payment URL to complete payment.")
                .data(orderService.checkout(request, httpRequest))
                .build());
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders() {
        return ResponseEntity.ok(ApiResponse.<List<OrderResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("My orders fetched successfully")
                .data(orderService.getMyOrders())
                .build());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Order detail fetched successfully")
                .data(orderService.getOrderDetail(orderId))
                .build());
    }
}
