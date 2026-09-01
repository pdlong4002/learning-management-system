package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.dto.request.ForgotPasswordRequest;
import com.ramennsama.springboot.lms.dto.request.PasswordUpdateRequest;
import com.ramennsama.springboot.lms.dto.request.ResetPasswordRequest;
import com.ramennsama.springboot.lms.dto.request.UserUpdateRequest;
import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.UserResponse;
import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;
import com.ramennsama.springboot.lms.dto.event.OtpEvent;
import com.ramennsama.springboot.lms.enums.OtpType;
import com.ramennsama.springboot.lms.enums.Role;
import org.springframework.context.ApplicationEventPublisher;
import com.ramennsama.springboot.lms.mapper.UserMapper;
import com.ramennsama.springboot.lms.repository.UserRepository;
import com.ramennsama.springboot.lms.service.UserService;
import com.ramennsama.springboot.lms.utils.FindAuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // 5 minutes validity
        userRepository.save(user);

        // Publish event for Async Email Sending
        OtpEvent event = OtpEvent.builder()
                .email(user.getEmail())
                .otpCode(otp)
                .durationInMinutes(5)
                .eventType(OtpType.RESET_PASSWORD)
                .build();
        eventPublisher.publishEvent(event);

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("OTP sent to your email successfully.")
                .build();
    }

    @Transactional
    @Override
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getOtpCode() == null || !user.getOtpCode().equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Password reset successfully. You can now login with your new password.")
                .build();
    }

    @Transactional
    @Override
    public void updatePassword(PasswordUpdateRequest rq) {
        User user = this.findAuthenticatedUser.getAuthenticatedUser();

        if (!isOldPasswordCorrect(user.getPassword(), rq.getOldPassword())) {
            throw new AppException(ErrorCode.CURRENT_PASSWORD_INCORRECT);
        }

        if (!isNewPasswordConfirmed(rq.getNewPassword(), rq.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        if (!isNewPasswordDifferent(rq.getNewPassword(), rq.getOldPassword())) {
            throw new AppException(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT);
        }

        user.setPassword(passwordEncoder.encode(rq.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public UserResponse updateAvatar(String imageUrl) {
        User user = this.findAuthenticatedUser.getAuthenticatedUser();
        user.setImageUrl(imageUrl);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponse updateProfile(UserUpdateRequest request) {
        User user = this.findAuthenticatedUser.getAuthenticatedUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getCurrentUser() {
        User user = this.findAuthenticatedUser.getAuthenticatedUser();
        return userMapper.toUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponse updateRole(String roleName) {
        User user = this.findAuthenticatedUser.getAuthenticatedUser();
        
        try {
            Role newRole = Role.valueOf(roleName.toUpperCase());
            // Chỉ cho phép chuyển đổi giữa STUDENT và INSTRUCTOR
            if (newRole == Role.STUDENT || newRole == Role.INSTRUCTOR) {
                user.setRole(newRole);
                userRepository.save(user);
            } else {
                throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
            }
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }
        
        return userMapper.toUserResponse(user);
    }

    /**
     * ======================== INTERNAL HELPERS ========================
     */

    private boolean isOldPasswordCorrect(String encodedPassword, String oldPassword) {
        return passwordEncoder.matches(oldPassword, encodedPassword);
    }

    private boolean isNewPasswordConfirmed(String newPassword, String confirmPassword) {
        return confirmPassword.equals(newPassword);
    }

    private boolean isNewPasswordDifferent(String newPassword, String oldPassword) {
        return !newPassword.equals(oldPassword);
    }
}
