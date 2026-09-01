# 🔐 Hướng dẫn luồng OAuth2 Login (Google & GitHub)

Tài liệu này mô tả chi tiết **từng bước** hệ thống OAuth2 hoạt động,
từ lúc user bấm nút "Đăng nhập bằng Google" cho đến khi nhận được JWT Token.

---

## 📋 Sơ đồ tổng quan

```
User (Browser)  →  Backend (Spring)  →  Google/GitHub  →  Backend  →  Frontend
     |                  |                    |                |            |
  Bấm Login       Lưu state          Xác thực user     Tạo JWT      Nhận token
               vào Cookie                              vào Cookie   (HttpOnly)
```

---

## 🚀 Luồng chi tiết từng bước

### Bước 1: User bấm nút "Đăng nhập bằng Google"
**Frontend** gửi request:
```
GET /oauth2/authorize/google?redirect_uri=http://localhost:3000/oauth2/redirect
```

### Bước 2: Spring lưu state vào Cookie (Chống CSRF)
**File:** `repository/HttpCookieOAuth2AuthorizationRequestRepository.java`
**Hàm:** `saveAuthorizationRequest()`

- Spring tự động tạo một chuỗi `state` ngẫu nhiên
- Serialize toàn bộ `OAuth2AuthorizationRequest` thành Base64
- Lưu vào Cookie tên `oauth2_auth_request` (sống 3 phút)
- Lưu `redirect_uri` vào Cookie riêng (để biết redirect Frontend về đâu sau này)

→ **Tiện ích dùng:** `utils/CookieUtils.addCookie()`, `CookieUtils.serialize()`

### Bước 3: Redirect user sang Google
Spring tự động redirect user đến:
```
https://accounts.google.com/o/oauth2/v2/auth?client_id=...&redirect_uri=...&state=abc123
```
User đăng nhập và cấp quyền trên giao diện Google.

### Bước 4: Google redirect về Backend
Google gọi callback:
```
GET /oauth2/callback/google?code=xxx&state=abc123
```

### Bước 5: Spring so khớp state (Chống CSRF)
**File:** `repository/HttpCookieOAuth2AuthorizationRequestRepository.java`
**Hàm:** `loadAuthorizationRequest()`

- Đọc Cookie `oauth2_auth_request`, deserialize ra `OAuth2AuthorizationRequest`
- So khớp `state` trong Cookie với `state` Google gửi về
- Nếu KHÁC → Chặn ngay (có thể bị tấn công CSRF)
- Nếu KHỚP → Tiếp tục

→ **Tiện ích dùng:** `utils/CookieUtils.getCookie()`, `CookieUtils.deserialize()`

### Bước 6: Spring đổi `code` lấy Access Token
Spring tự động gọi Google API:
```
POST https://oauth2.googleapis.com/token
Body: code=xxx&client_id=...&client_secret=...
```
Nhận về Access Token của Google.

### Bước 7: Lấy thông tin user từ Google
**File:** `service/CustomOAuth2UserService.java`
**Hàm:** `loadUser()`

- Spring dùng Access Token gọi Google API lấy thông tin user
- `super.loadUser()` trả về `OAuth2User` chứa dữ liệu thô (JSON)
- Chuyển sang `processOAuth2User()` để xử lý nghiệp vụ

### Bước 8: Chuẩn hóa thông tin user
**File:** `user/OAuth2UserInfoFactory.java`
**Hàm:** `getOAuth2UserInfo()`

- Nhận `registrationId` = "google" → trả về `GoogleOAuth2UserInfo`
- Nhận `registrationId` = "github" → trả về `GithubOAuth2UserInfo`
- Mỗi class con biết cách "bóc" dữ liệu đúng field (Google dùng `sub`, GitHub dùng `id`)

→ **Class liên quan:** `user/OAuth2UserInfo.java` (class cha, chứa helper `getStringAttribute()`)

### Bước 9: Kiểm tra & Lưu user vào Database
**File:** `service/CustomOAuth2UserService.java`
**Hàm:** `processOAuth2User()`

| Trường hợp | Hành động | Hàm gọi |
|---|---|---|
| Email CHƯA có trong DB | Tạo tài khoản mới (role = STUDENT) | `registerNewUser()` |
| Email ĐÃ CÓ + đúng provider | Cập nhật avatar, tên | `updateExistingUser()` |
| Email ĐÃ CÓ + SAI provider | Ném lỗi: "Bạn đã đăng ký bằng Google, hãy dùng Google để đăng nhập" | throw exception |
| Email KHÔNG CÓ từ provider | Ném lỗi: "Email not found" | throw exception |

### Bước 10: Tạo OAuth2UserPrincipal
**File:** `OAuth2UserPrincipal.java`
**Hàm:** `create(user, attributes)`

- Đóng gói User entity + attributes gốc thành `OAuth2UserPrincipal`
- Class này implements cả `OAuth2User` + `UserDetails`
- Spring Security dùng nó làm "principal" (đại diện user đã đăng nhập)

### Bước 11: Tạo JWT Token & Đặt vào HttpOnly Cookie
**File:** `handler/OAuth2AuthenticationSuccessHandler.java`
**Hàm:** `onAuthenticationSuccess()` → `determineTargetUrl()`

1. Đọc `redirect_uri` từ Cookie (Frontend muốn redirect về đâu?)
2. Kiểm tra URI có hợp lệ không → `isAuthorizedRedirectUri()` (chống Open Redirect)
3. Tạo JWT Token: `jwtService.generateToken(principal)`
4. **Đặt JWT vào HttpOnly Cookie** tên `accessToken` (sống 15 phút)
   - JavaScript KHÔNG THỂ đọc được cookie này → Chống XSS
   - Trình duyệt TỰ ĐỘNG gửi kèm cookie trong mọi request → Không cần header Authorization
5. Redirect Frontend về: `http://localhost:3000/oauth2/redirect?success=true`

### Bước 12: Dọn dẹp Cookie tạm
**File:** `handler/OAuth2AuthenticationSuccessHandler.java`
**Hàm:** `clearAuthenticationAttributes()`

- Xóa session attributes
- Gọi `httpCookieRepo.removeAuthorizationRequestCookies()` để xóa 2 cookie tạm:
  - `oauth2_auth_request`
  - `redirect_uri`

---

## ❌ Khi đăng nhập THẤT BẠI

**File:** `handler/OAuth2AuthenticationFailureHandler.java`
**Hàm:** `onAuthenticationFailure()`

1. Đọc `redirect_uri` từ Cookie
2. Gắn thông báo lỗi: `?error=User denied access`
3. Dọn dẹp cookie OAuth2 tạm thời
4. Redirect về Frontend: `http://localhost:3000/oauth2/redirect?error=User+denied+access`

---

## 📁 Bản đồ file & Vai trò

| File | Vai trò | Được gọi bởi |
|---|---|---|
| `utils/CookieUtils.java` | Tiện ích đọc/ghi/xóa/serialize Cookie | Tất cả các file khác |
| `user/OAuth2UserInfo.java` | Class cha định nghĩa hợp đồng chung | Factory, Service |
| `user/GoogleOAuth2UserInfo.java` | Bóc dữ liệu từ Google (sub, picture) | Factory |
| `user/GithubOAuth2UserInfo.java` | Bóc dữ liệu từ GitHub (id, avatar_url, login) | Factory |
| `user/OAuth2UserInfoFactory.java` | Chọn class phù hợp theo provider | Service |
| `OAuth2UserPrincipal.java` | Đại diện user đã xác thực (Principal) | Service, SuccessHandler |
| `service/CustomOAuth2UserService.java` | Xử lý nghiệp vụ: Đăng ký/Cập nhật user | Spring Security (tự động) |
| `repository/HttpCookie...Repository.java` | Lưu state chống CSRF vào Cookie | Spring Security (tự động) |
| `handler/...SuccessHandler.java` | Tạo JWT + HttpOnly Cookie khi thành công | Spring Security (tự động) |
| `handler/...FailureHandler.java` | Redirect kèm lỗi khi thất bại | Spring Security (tự động) |

---

## 🔑 Các cơ chế bảo mật

| Mối đe dọa | Giải pháp | File thực hiện |
|---|---|---|
| **XSS** (đánh cắp token bằng JavaScript) | JWT trong HttpOnly Cookie | SuccessHandler |
| **CSRF** (giả mạo request đăng nhập) | State Cookie + so khớp | CookieRepository |
| **Open Redirect** (redirect về trang giả) | Whitelist URI (host + port) | SuccessHandler |
| **Đăng nhập chéo provider** | Kiểm tra provider trong DB | CustomOAuth2UserService |
