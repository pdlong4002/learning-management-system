package com.ramennsama.springboot.lms.oauth2.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

/**
 * Tiện ích thao tác Cookie.
 * Cung cấp các phương thức tĩnh để đọc, ghi, xóa và serialize/deserialize Cookie.
 * Được sử dụng chủ yếu trong quá trình OAuth2 để lưu trữ state chống CSRF.
 */
public class CookieUtils {

    /**
     * Tìm và trả về Cookie theo tên từ HttpServletRequest.
     *
     * @param request HTTP request chứa danh sách cookie
     * @param name    tên cookie cần tìm
     * @return Optional chứa Cookie nếu tìm thấy, hoặc empty nếu không có
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Tạo và thêm một Cookie mới vào HttpServletResponse.
     * Cookie được đặt mặc định là HttpOnly (JavaScript không đọc được) để chống XSS.
     *
     * @param response HTTP response để gắn cookie vào
     * @param name     tên cookie
     * @param value    giá trị cookie
     * @param maxAge   thời gian sống (giây). Ví dụ: 180 = 3 phút
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * Xóa Cookie bằng cách đặt maxAge = 0 (trình duyệt sẽ tự hủy).
     *
     * @param request  HTTP request để tìm cookie hiện có
     * @param response HTTP response để ghi đè cookie với maxAge=0
     * @param name     tên cookie cần xóa
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    /**
     * Chuyển đổi một Object thành chuỗi Base64 để lưu vào Cookie.
     * Ví dụ: OAuth2AuthorizationRequest → Base64 String → Cookie value.
     *
     * @param object đối tượng cần serialize
     * @return chuỗi Base64 URL-safe
     */
    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    /**
     * Khôi phục Object từ chuỗi Base64 trong Cookie.
     * Ngược lại với serialize(): Cookie value → Base64 decode → Object.
     *
     * @param cookie cookie chứa giá trị Base64
     * @param cls    class của đối tượng cần khôi phục
     * @return đối tượng đã được deserialize
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
