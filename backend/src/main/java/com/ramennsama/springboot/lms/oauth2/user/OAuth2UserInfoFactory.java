package com.ramennsama.springboot.lms.oauth2.user;

import com.ramennsama.springboot.lms.enums.AuthProvider;
import com.ramennsama.springboot.lms.exception.OAuth2AuthenticationProcessingException;

import java.util.Map;

/**
 * Factory Pattern: Tự động chọn class xử lý phù hợp dựa trên nhà cung cấp OAuth2.
 * Ví dụ: registrationId = "google" → trả về GoogleOAuth2UserInfo,
 *         registrationId = "github" → trả về GithubOAuth2UserInfo.
 * Nếu nhà cung cấp không được hỗ trợ → ném OAuth2AuthenticationProcessingException.
 */
public class OAuth2UserInfoFactory {

    /**
     * Tạo instance OAuth2UserInfo phù hợp dựa trên tên nhà cung cấp.
     *
     * @param registrationId tên đăng ký của nhà cung cấp (vd: "google", "github")
     * @param attributes     dữ liệu gốc trả về từ API của nhà cung cấp
     * @return instance tương ứng (GoogleOAuth2UserInfo hoặc GithubOAuth2UserInfo)
     * @throws OAuth2AuthenticationProcessingException nếu nhà cung cấp không được hỗ trợ
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.toString())) {
            return new GithubOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
