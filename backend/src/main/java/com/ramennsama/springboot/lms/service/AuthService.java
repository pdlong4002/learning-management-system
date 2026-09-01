package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.dto.request.LoginRequest;
import com.ramennsama.springboot.lms.dto.request.RegisterRequest;
import com.ramennsama.springboot.lms.dto.response.TokenResponse;

import com.ramennsama.springboot.lms.dto.response.ApiResponse;

import com.ramennsama.springboot.lms.dto.request.VerifyEmailRequest;

public interface AuthService {
    ApiResponse<String> register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse verifyEmail(VerifyEmailRequest request);
}
