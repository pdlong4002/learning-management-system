package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.dto.request.ForgotPasswordRequest;
import com.ramennsama.springboot.lms.dto.request.PasswordUpdateRequest;
import com.ramennsama.springboot.lms.dto.request.ResetPasswordRequest;
import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.UserResponse;

import com.ramennsama.springboot.lms.dto.request.UserUpdateRequest;

public interface UserService {
    ApiResponse<String> forgotPassword(ForgotPasswordRequest request);
    ApiResponse<String> resetPassword(ResetPasswordRequest request);
    void updatePassword(PasswordUpdateRequest request);
    UserResponse updateAvatar(String imageUrl);
    UserResponse updateProfile(UserUpdateRequest request);
    UserResponse getCurrentUser();
    UserResponse updateRole(String role);
}
