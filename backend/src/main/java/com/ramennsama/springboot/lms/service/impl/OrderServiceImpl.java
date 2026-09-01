package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.dto.request.CheckoutRequest;
import com.ramennsama.springboot.lms.dto.response.CheckoutResponse;
import com.ramennsama.springboot.lms.dto.response.OrderResponse;
import com.ramennsama.springboot.lms.entity.Course;
import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.entity.OrderItem;
import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.enums.OrderStatus;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;
import com.ramennsama.springboot.lms.mapper.OrderMapper;
import com.ramennsama.springboot.lms.repository.CourseRepository;
import com.ramennsama.springboot.lms.repository.EnrollmentRepository;
import com.ramennsama.springboot.lms.repository.OrderRepository;
import com.ramennsama.springboot.lms.service.OrderService;
import com.ramennsama.springboot.lms.service.PaymentService;
import com.ramennsama.springboot.lms.utils.FindAuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OrderMapper orderMapper;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request, HttpServletRequest httpRequest) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        List<Course> coursesToBuy = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Long courseId : request.getCourseIds()) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

            // Kiểm tra xem user đã mua khóa học này chưa
            if (enrollmentRepository.existsByUserIdAndCourseId(currentUser.getId(), courseId)) {
                throw new AppException(ErrorCode.ALREADY_ENROLLED);
            }

            coursesToBuy.add(course);
            totalPrice = totalPrice.add(course.getPrice() != null ? course.getPrice() : BigDecimal.ZERO);
        }

        // Tạo Order với trạng thái PENDING (không còn fake SUCCESS)
        Order order = Order.builder()
                .user(currentUser)
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .build();

        List<OrderItem> orderItems = coursesToBuy.stream().map(course -> OrderItem.builder()
                .order(order)
                .course(course)
                .price(course.getPrice() != null ? course.getPrice() : BigDecimal.ZERO)
                .build()).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        log.info("Order {} created (PENDING) for user {}. Total: {}",
                order.getId(), currentUser.getEmail(), totalPrice);

        // Gọi PaymentService để tạo URL thanh toán
        String paymentUrl = paymentService.createPaymentUrl(order, request.getPaymentMethod(), httpRequest);

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .paymentUrl(paymentUrl)
                .paymentMethod(request.getPaymentMethod())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();
        return orderRepository.findByUserId(currentUser.getId()).stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long orderId) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        return orderMapper.toOrderResponse(order);
    }
}
