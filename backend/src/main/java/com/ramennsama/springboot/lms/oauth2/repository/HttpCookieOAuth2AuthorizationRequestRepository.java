package com.ramennsama.springboot.lms.oauth2.repository;

import com.ramennsama.springboot.lms.oauth2.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Lưu trữ OAuth2 Authorization Request vào Cookie thay vì Session (vì app là Stateless).
 * Đây là cơ chế chống CSRF: Khi user bấm "Đăng nhập bằng Google", hệ thống tạo một chuỗi "state"
 * ngẫu nhiên và lưu vào Cookie. Khi Google redirect về, hệ thống so khớp "state" này.
 * Nếu sai → chặn ngay (hacker không thể giả mạo).
 *
 * Vòng đời Cookie:
 * 1. saveAuthorizationRequest()  → Tạo cookie khi bắt đầu đăng nhập
 * 2. loadAuthorizationRequest()  → Đọc cookie khi Google redirect về
 * 3. removeAuthorizationRequest() → Đánh dấu xóa sau khi xác thực xong
 * 4. removeAuthorizationRequestCookies() → Dọn dẹp tất cả cookie OAuth2
 */
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    /** Tên cookie chứa OAuth2AuthorizationRequest đã serialize. */
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    /** Tên cookie chứa URL mà Frontend muốn redirect tới sau khi đăng nhập thành công. */
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";

    /** Thời gian sống của cookie (180 giây = 3 phút). Đủ để user hoàn tất đăng nhập trên Google/GitHub. */
    private static final int cookieExpireSeconds = 180;

    /**
     * [BƯỚC 2] Đọc lại OAuth2AuthorizationRequest từ Cookie.
     * Được Spring gọi khi Google/GitHub redirect user quay lại app.
     * Dùng để so khớp "state" chống CSRF.
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    /**
     * [BƯỚC 1] Lưu OAuth2AuthorizationRequest vào Cookie.
     * Được Spring gọi trước khi redirect user sang Google/GitHub.
     * Nếu authorizationRequest == null → xóa cookie (dọn dẹp).
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
            return;
        }

        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(authorizationRequest), cookieExpireSeconds);
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        if (StringUtils.hasText(redirectUriAfterLogin)) {
            CookieUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds);
        }
    }

    /**
     * [BƯỚC 3] Đánh dấu xóa Authorization Request (Spring gọi sau khi xác thực xong).
     * Trả về request hiện tại để Spring so khớp "state" lần cuối trước khi hủy.
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    /**
     * [BƯỚC 4] Dọn dẹp toàn bộ cookie OAuth2.
     * Được gọi bởi SuccessHandler và FailureHandler sau khi xử lý xong.
     */
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }
}
