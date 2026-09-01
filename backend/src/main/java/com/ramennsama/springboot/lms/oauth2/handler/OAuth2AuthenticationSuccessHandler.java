package com.ramennsama.springboot.lms.oauth2.handler;

import com.ramennsama.springboot.lms.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.ramennsama.springboot.lms.service.JwtService;
import com.ramennsama.springboot.lms.oauth2.utils.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/**
 * Xử lý SAU KHI đăng nhập OAuth2 THÀNH CÔNG.
 * Đây là nơi "phép thuật" xảy ra: Tạo JWT Token và đóng gói vào HttpOnly
 * Cookie.
 *
 * Luồng xử lý:
 * 1. Google/GitHub xác nhận user hợp lệ → Spring gọi onAuthenticationSuccess()
 * 2. Tạo JWT Token từ thông tin user
 * 3. Nhét JWT vào HttpOnly Cookie (JavaScript không đọc được → chống XSS)
 * 4. Redirect user về Frontend kèm ?success=true
 * 5. Dọn dẹp cookie OAuth2 tạm thời
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    /**
     * URL Frontend được phép nhận redirect. Mặc định:
     * http://localhost:3000/oauth2/redirect
     */
    @Value("${app.oauth2.authorizedRedirectUris:http://localhost:3000/oauth2/redirect}")
    private String authorizedRedirectUri;

    /**
     * [ĐIỂM VÀO] Được Spring Security gọi khi OAuth2 login thành công.
     * Xác định URL đích → Dọn dẹp cookie → Redirect user.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Xác định URL redirect và tạo JWT Token.
     * 1. Đọc redirect_uri từ cookie (Frontend gửi lên khi bắt đầu đăng nhập)
     * 2. Kiểm tra URI có nằm trong danh sách được phép không (chống open redirect
     * attack)
     * 3. Tạo JWT Token từ Authentication principal
     * 4. Đặt JWT vào HttpOnly Cookie (sống 15 phút)
     * 5. Trả về URL kèm ?success=true
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        Optional<String> redirectUri = CookieUtils
                .getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException(
                    "Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        // Tạo JWT Token từ thông tin user đã xác thực
        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());

        // Đặt JWT vào HttpOnly Cookie - JavaScript KHÔNG THỂ đọc được (chống XSS)
        CookieUtils.addCookie(response, "accessToken", token, 900); // 900 giây = 15 phút

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("success", "true")
                .build().toUriString();
    }

    /**
     * Dọn dẹp: Xóa session attributes + cookie OAuth2 tạm thời.
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    /**
     * Kiểm tra redirect URI có hợp lệ không (cùng host + port với URI được cấu
     * hình).
     * Chống tấn công Open Redirect: Hacker không thể redirect user về trang giả
     * mạo.
     */
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedURI = URI.create(authorizedRedirectUri);

        return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedURI.getPort() == clientRedirectUri.getPort();
    }
}
