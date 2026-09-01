package com.ramennsama.springboot.lms.oauth2.handler;

import com.ramennsama.springboot.lms.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.ramennsama.springboot.lms.oauth2.utils.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Xử lý SAU KHI đăng nhập OAuth2 THẤT BẠI.
 * Ví dụ: User từ chối cấp quyền trên Google, hoặc email không hợp lệ.
 *
 * Luồng xử lý:
 * 1. Đọc redirect_uri từ cookie (hoặc mặc định về "/")
 * 2. Gắn thông báo lỗi vào URL: ?error=...
 * 3. Dọn dẹp cookie OAuth2 tạm thời
 * 4. Redirect user về Frontend kèm thông tin lỗi
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    /**
     * [ĐIỂM VÀO] Được Spring Security gọi khi OAuth2 login thất bại.
     * Redirect user về Frontend với query param ?error=<thông báo lỗi>.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // Lấy redirect_uri từ cookie, nếu không có thì redirect về trang chủ "/"
        String targetUrl = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(("/"));

        // Gắn thông báo lỗi vào URL
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        // Dọn dẹp cookie OAuth2 tạm thời
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
