package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.dto.request.LoginRequest;
import com.ramennsama.springboot.lms.dto.request.RegisterRequest;
import com.ramennsama.springboot.lms.dto.request.ForgotPasswordRequest;
import com.ramennsama.springboot.lms.dto.request.ResetPasswordRequest;
import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.TokenResponse;
import com.ramennsama.springboot.lms.dto.response.UserResponse;
import com.ramennsama.springboot.lms.entity.RefreshToken;
import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.enums.Role;
import com.ramennsama.springboot.lms.enums.AuthProvider;
import com.ramennsama.springboot.lms.repository.RefreshTokenRepository;
import com.ramennsama.springboot.lms.repository.UserRepository;
import com.ramennsama.springboot.lms.service.AuthService;
import com.ramennsama.springboot.lms.service.EmailService;
import com.ramennsama.springboot.lms.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import com.ramennsama.springboot.lms.dto.event.OtpEvent;
import com.ramennsama.springboot.lms.dto.request.VerifyEmailRequest;
import com.ramennsama.springboot.lms.enums.OtpType;
import org.springframework.context.ApplicationEventPublisher;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.auth.refreshTokenExpiration}")
    private long refreshTokenDurationMs;

    @Transactional
    public ApiResponse<String> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        Role userRole = Role.STUDENT;
        if ("INSTRUCTOR".equalsIgnoreCase(request.getRole())) {
            userRole = Role.INSTRUCTOR;
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .provider(AuthProvider.LOCAL)
                .enabled(false) // Wait for email verification
                .otpCode(otp)
                .otpExpiry(LocalDateTime.now().plusMinutes(5))
                .build();

        userRepository.save(user);

        // Publish event for Async Email Sending
        OtpEvent event = OtpEvent.builder()
                .email(user.getEmail())
                .otpCode(otp)
                .durationInMinutes(5)
                .eventType(OtpType.REGISTER_OTP)
                .build();
        eventPublisher.publishEvent(event);

        // DEBUG: Print OTP to console to help with local testing
        System.out.println("==========================================================");
        System.out.println("MÃ OTP XÁC THỰC CHO " + request.getEmail() + " LÀ: " + otp);
        System.out.println("==========================================================");

        return ApiResponse.<String>builder()
                .status(HttpStatus.CREATED.value())
                .message("User registered successfully. Please check your email for OTP verification.")
                .build();
    }

    @Transactional
    public TokenResponse verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        System.out.println("DEBUG VERIFY: DB enabled = " + user.isEnabled());
        if (user.isEnabled()) {
            // If already verified, just let them in!
            System.out.println("DEBUG VERIFY: Already enabled, returning tokens.");
            return generateTokens(user);
        }

        System.out.println("DEBUG VERIFY: DB OTP = " + user.getOtpCode() + ", Request OTP = " + request.getOtp());
        System.out.println("DEBUG VERIFY: DB Expiry = " + user.getOtpExpiry() + ", Current Time = " + LocalDateTime.now());

        // Magic OTP for local testing
        if (!"000000".equals(request.getOtp())) {
            if (user.getOtpCode() == null || !user.getOtpCode().equals(request.getOtp())) {
                System.out.println("DEBUG VERIFY: OTP mismatch!");
                throw new AppException(ErrorCode.INVALID_OTP);
            }

            if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
                System.out.println("DEBUG VERIFY: OTP expired!");
                throw new AppException(ErrorCode.OTP_EXPIRED);
            }
        }

        user.setEnabled(true);
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return generateTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return generateTokens(user);
    }

    private TokenResponse generateTokens(User user) {
        String accessToken = jwtService.generateToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        refreshTokenRepository.save(refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
