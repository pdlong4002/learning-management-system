package com.ramennsama.springboot.lms.oauth2.service;

import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.enums.AuthProvider;
import com.ramennsama.springboot.lms.enums.Role;
import com.ramennsama.springboot.lms.exception.OAuth2AuthenticationProcessingException;
import com.ramennsama.springboot.lms.oauth2.OAuth2UserPrincipal;
import com.ramennsama.springboot.lms.oauth2.user.OAuth2UserInfo;
import com.ramennsama.springboot.lms.oauth2.user.OAuth2UserInfoFactory;
import com.ramennsama.springboot.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Service cốt lõi xử lý thông tin user sau khi đăng nhập OAuth2 thành công.
 * Được Spring Security tự động gọi sau khi Google/GitHub trả về thông tin user.
 *
 * Nhiệm vụ chính:
 * 1. Nhận dữ liệu thô từ nhà cung cấp OAuth2
 * 2. Chuẩn hóa thông tin qua OAuth2UserInfoFactory
 * 3. Kiểm tra user đã tồn tại trong DB chưa → Đăng ký mới hoặc cập nhật
 * 4. Trả về OAuth2UserPrincipal để Spring Security tiếp tục xử lý
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * [ĐIỂM VÀO CHÍNH] Spring Security gọi method này sau khi nhận được access token từ Google/GitHub.
     * Gọi API của nhà cung cấp để lấy thông tin user, rồi chuyển sang processOAuth2User() xử lý.
     *
     * @param oAuth2UserRequest chứa access token và thông tin client registration
     * @return OAuth2User (OAuth2UserPrincipal) đại diện cho user đã xác thực
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Xử lý logic nghiệp vụ chính:
     * 1. Dùng Factory để chuẩn hóa thông tin user (Google/GitHub → format chung)
     * 2. Kiểm tra email có tồn tại trong DB không
     *    - Có → Kiểm tra provider có khớp không (chống đăng nhập chéo provider)
     *    - Không → Tạo tài khoản mới tự động
     * 3. Trả về OAuth2UserPrincipal chứa thông tin user + attributes gốc
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes()
        );

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Chặn đăng nhập chéo provider: VD đã đăng ký bằng Google nhưng bấm nút GitHub
            if (!user.getProvider().name().equalsIgnoreCase(oAuth2UserRequest.getClientRegistration().getRegistrationId())) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return OAuth2UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    /**
     * Đăng ký tài khoản mới cho user OAuth2 lần đầu đăng nhập.
     * Tự động tách họ/tên từ fullname, gán role mặc định là STUDENT.
     */
    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setEmail(oAuth2UserInfo.getEmail());
        
        String name = oAuth2UserInfo.getName();
        if (name != null) {
            String[] nameParts = name.split(" ", 2);
            user.setFirstName(nameParts[0]);
            if (nameParts.length > 1) {
                user.setLastName(nameParts[1]);
            }
        }
        
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        user.setRole(Role.STUDENT);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    /**
     * Cập nhật thông tin cho user đã tồn tại (avatar, tên).
     * Mỗi lần đăng nhập lại, thông tin mới nhất từ Google/GitHub sẽ được sync.
     */
    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        
        String name = oAuth2UserInfo.getName();
        if (name != null) {
            String[] nameParts = name.split(" ", 2);
            existingUser.setFirstName(nameParts[0]);
            if (nameParts.length > 1) {
                existingUser.setLastName(nameParts[1]);
            }
        }
        
        return userRepository.save(existingUser);
    }
}
