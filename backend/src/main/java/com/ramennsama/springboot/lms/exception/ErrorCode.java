package com.ramennsama.springboot.lms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_DATA("COMMON_001", "Invalid data", HttpStatus.BAD_REQUEST),
    INVALID_INPUT("USER_999", "Invalid input", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("COMMON_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Authentication & Authorization
    UNAUTHENTICATED("AUTH_001", "Authentication required", HttpStatus.UNAUTHORIZED),
    BAD_CREDENTIALS("AUTH_002", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH_003", "Account is locked", HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED("AUTH_004", "Account is disabled", HttpStatus.FORBIDDEN),
    ACCESS_DENIED("AUTH_005", "You do not have permission to access this resource", HttpStatus.FORBIDDEN),
    AUTH_PROVIDER_NOT_SUPPORTED("AUTH_006", "Authentication provider not supported", HttpStatus.BAD_REQUEST),
    INVALID_OTP("AUTH_007", "Invalid OTP", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED("AUTH_008", "OTP has expired", HttpStatus.BAD_REQUEST),
    CURRENT_PASSWORD_INCORRECT("AUTH_009", "Current password is incorrect", HttpStatus.BAD_REQUEST),
    PASSWORDS_DO_NOT_MATCH("AUTH_010", "New passwords do not match", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_MUST_BE_DIFFERENT("AUTH_011", "Old and new passwords must be different", HttpStatus.BAD_REQUEST),

    // Token
    INVALID_TOKEN("TOKEN_001", "Token is invalid or expired", HttpStatus.UNAUTHORIZED),

    // User & Validation
    USER_NOT_FOUND("USER_001", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_002", "Email is already registered", HttpStatus.CONFLICT),

    // Course & Learning
    CATEGORY_NOT_FOUND("COURSE_001", "Category not found", HttpStatus.NOT_FOUND),
    COURSE_NOT_FOUND("COURSE_002", "Course not found", HttpStatus.NOT_FOUND),
    SECTION_NOT_FOUND("COURSE_003", "Section not found", HttpStatus.NOT_FOUND),
    LESSON_NOT_FOUND("COURSE_004", "Lesson not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACTION("COURSE_005", "You do not have permission to perform this action", HttpStatus.FORBIDDEN),
    ALREADY_ENROLLED("COURSE_006", "You are already enrolled in this course", HttpStatus.BAD_REQUEST),
    NOT_ENROLLED("COURSE_007", "You must enroll in this course first", HttpStatus.FORBIDDEN),
    ALREADY_REVIEWED("COURSE_008", "You have already reviewed this course", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND("COURSE_009", "Review not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND("ORDER_001", "Order not found", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_PAID("ORDER_002", "Order has already been paid", HttpStatus.BAD_REQUEST),
    ORDER_EXPIRED("ORDER_003", "Order has expired", HttpStatus.BAD_REQUEST),

    // Payment
    PAYMENT_PROVIDER_NOT_SUPPORTED("PAYMENT_001", "Payment provider is not supported", HttpStatus.BAD_REQUEST),
    PAYMENT_VERIFICATION_FAILED("PAYMENT_002", "Payment verification failed - invalid signature", HttpStatus.BAD_REQUEST),

    // Coupon
    COUPON_NOT_FOUND("COUPON_001", "Coupon not found", HttpStatus.NOT_FOUND),
    COUPON_ALREADY_EXISTS("COUPON_002", "Coupon already exists", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
