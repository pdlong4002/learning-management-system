package com.ramennsama.springboot.lms.oauth2.user;

import java.util.Map;

/**
 * Class trừu tượng (abstract) đại diện cho thông tin người dùng từ nhà cung cấp OAuth2.
 * Mỗi nhà cung cấp (Google, GitHub) trả về dữ liệu khác nhau,
 * class này định nghĩa "hợp đồng" chung mà tất cả phải tuân thủ.
 *
 * @see GoogleOAuth2UserInfo
 * @see GithubOAuth2UserInfo
 */
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /** Trả về toàn bộ dữ liệu gốc (raw) từ nhà cung cấp OAuth2. */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Lấy giá trị attribute theo key một cách an toàn (null-safe).
     * Tránh NullPointerException khi giá trị không tồn tại,
     * và tránh ClassCastException khi kiểu dữ liệu không phải String.
     *
     * @param key tên attribute cần lấy
     * @return giá trị dạng String, hoặc null nếu không tồn tại
     */
    protected String getStringAttribute(String key) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : null;
    }

    /** Trả về ID duy nhất của người dùng từ nhà cung cấp (Google: "sub", GitHub: "id"). */
    public abstract String getId();

    /** Trả về tên hiển thị của người dùng. */
    public abstract String getName();

    /** Trả về email của người dùng. */
    public abstract String getEmail();

    /** Trả về URL ảnh đại diện (avatar) của người dùng. */
    public abstract String getImageUrl();
}
