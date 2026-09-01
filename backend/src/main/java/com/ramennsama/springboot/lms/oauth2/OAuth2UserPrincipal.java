package com.ramennsama.springboot.lms.oauth2;

import com.ramennsama.springboot.lms.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Đối tượng đại diện cho người dùng đã xác thực thành công qua OAuth2.
 * Implements cả OAuth2User (cho Spring OAuth2) và UserDetails (cho Spring Security),
 * giúp hệ thống xử lý thống nhất dù user đăng nhập bằng Google, GitHub hay email/password.
 *
 * Đây là "cầu nối" giữa thông tin từ nhà cung cấp OAuth2 và hệ thống bảo mật nội bộ.
 */
public class OAuth2UserPrincipal implements OAuth2User, UserDetails {

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public OAuth2UserPrincipal(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Tạo OAuth2UserPrincipal từ entity User trong database.
     * Chuyển đổi Role của User thành GrantedAuthority (vd: STUDENT → ROLE_STUDENT).
     *
     * @param user entity User từ database
     * @return OAuth2UserPrincipal chứa thông tin bảo mật
     */
    public static OAuth2UserPrincipal create(User user) {
        return new OAuth2UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    /**
     * Tạo OAuth2UserPrincipal từ entity User + attributes gốc từ nhà cung cấp OAuth2.
     * Dùng khi cần giữ lại dữ liệu gốc (ví dụ: avatar URL, locale) từ Google/GitHub.
     *
     * @param user       entity User từ database
     * @param attributes dữ liệu gốc từ nhà cung cấp OAuth2
     * @return OAuth2UserPrincipal đầy đủ thông tin
     */
    public static OAuth2UserPrincipal create(User user, Map<String, Object> attributes) {
        OAuth2UserPrincipal userPrincipal = OAuth2UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /** Trả về email làm username (Spring Security dùng để định danh user). */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /** Trả về danh sách quyền (vd: ROLE_STUDENT, ROLE_INSTRUCTOR). */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /** Trả về dữ liệu gốc từ nhà cung cấp OAuth2 (Google/GitHub). */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /** Trả về ID dạng String (yêu cầu bởi interface OAuth2User). */
    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
