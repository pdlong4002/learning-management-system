package com.ramennsama.springboot.lms.controller;

import com.ramennsama.springboot.lms.dto.request.ForgotPasswordRequest;
import com.ramennsama.springboot.lms.dto.request.PasswordUpdateRequest;
import com.ramennsama.springboot.lms.dto.request.ResetPasswordRequest;
import com.ramennsama.springboot.lms.dto.request.UserUpdateRequest;
import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.UserResponse;
import com.ramennsama.springboot.lms.service.UserService;
import jakarta.validation.Valid;
import com.ramennsama.springboot.lms.service.JwtService;
import com.ramennsama.springboot.lms.oauth2.utils.CookieUtils;
import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.utils.FindAuthenticatedUser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final FindAuthenticatedUser findAuthenticatedUser;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(userService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(200)
                .message("Password updated successfully")
                .build());
    }

    @PutMapping("/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> updateAvatar(@RequestBody Map<String, String> body) {
        String imageUrl = body.get("imageUrl");
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .status(200)
                .message("Avatar updated successfully")
                .data(userService.updateAvatar(imageUrl))
                .build());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .status(200)
                .message("Profile updated successfully")
                .data(userService.updateProfile(request))
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .status(200)
                .message("User fetched successfully")
                .data(userService.getCurrentUser())
                .build());
    }

    @PatchMapping("/role")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateRole(
            @RequestBody Map<String, String> body,
            HttpServletResponse response) {
        String newRole = body.get("role");
        UserResponse updatedUser = userService.updateRole(newRole);
        
        // Cấp lại JWT Token mới vì Token cũ đang chứa Role cũ trong Payload
        User user = findAuthenticatedUser.getAuthenticatedUser();
        String newToken = jwtService.generateToken(user);
        
        // Cập nhật lại HttpOnly Cookie cho OAuth2 flow
        CookieUtils.addCookie(response, "accessToken", newToken, 900);

        Map<String, Object> data = new HashMap<>();
        data.put("user", updatedUser);
        data.put("accessToken", newToken);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .message("Role updated successfully")
                .data(data)
                .build());
    }
}
